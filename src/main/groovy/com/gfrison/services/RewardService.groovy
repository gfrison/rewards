package com.gfrison.services

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import org.apache.camel.ProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate


/**
 * @author giancarlo@gfrison.com <Giancarlo Frison>
 *
 */
@Slf4j
@Path("/rewards")
class RewardService {
	public enum EligibilityOutput{CUSTOMER_ELIGIBLE,CUSTOMER_INELIGIBLE }
	public enum Channel{SPORTS, KIDS, MUSIC, NEWS, MOVIES}
	

	@Autowired
	private ProducerTemplate producer
	
	@Autowired
	private JdbcTemplate db
	
	@GET
	@Path("/account/{accountId}")
	public Response getRewards(@PathParam("accountId")  String accountId, String portfolioJson) {
		if(!accountId || !portfolioJson)
			return Response.status(Status.BAD_REQUEST).build()
		def slurper = new JsonSlurper()
		def json = slurper.parseText(portfolioJson)
		if(json.portfolio?.channels?.isEmpty())
			return Response.status(Status.BAD_REQUEST).build()
		def eligibility = producer.requestBody(accountId)
		log.info "account:${accountId}, eligible response:"+eligibility
		def jsonReply = new JsonBuilder()
		switch(eligibility){
			case EligibilityOutput.CUSTOMER_ELIGIBLE.name():
				def channels = json.portfolio.channels.collect{"'"+it+"'"}.join(',')
				log.info "channels:${channels}"
				List<String> dbrewards = db.queryForList("select reward from rewards where channel in (${channels})",String.class)
				log.info "rewards:${dbrewards}"
				jsonReply.account{rewards dbrewards}
				return Response.ok(jsonReply.toString()).build()
			break;
		}
		return Response.ok(eligibility).build()
	}
	
	/**
	* query with JDBCTemplate but if value is not found, will return null instead of Exception
	*
	* @param <T>
	* @param db
	* @param sql
	* @param requiredType
	* @param args
	* @return
	*/
   public <T> T queryFor(String sql,  Class<T> requiredType, Object... args){
	   try {
		   return db.queryForObject(sql, args, requiredType);
	   } catch (EmptyResultDataAccessException e) {
		   return null;
	   }
   }

}
