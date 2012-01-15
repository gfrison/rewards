package com.gfrison.services

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import org.apache.camel.CamelExecutionException
import org.apache.camel.ProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
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
	private ProducerTemplate eligibility
	
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
		log.info "account:${accountId}, portfolio:${portfolioJson}"
		def jsonReply = new JsonBuilder()
		
		//perform request to EligibilityService
		def eligibilityResponse
		try{
			eligibilityResponse = eligibility.requestBody(accountId)
		}catch(CamelExecutionException e){
			if(e.getCause() instanceof RuntimeException){
				//503 Service Unavailable (Technical errors)
				return Response.status(503).build()
			}else {
				//400 Bad Request (Invalid account number)
				jsonReply.account{error e.getMessage()}
				return Response.status(400).entity(jsonReply.toString()).build()
			}
		}
		log.info "account:${accountId}, eligible response:"+eligibilityResponse
		switch(eligibilityResponse){
			case EligibilityOutput.CUSTOMER_ELIGIBLE.name():
				def channels = json.portfolio.channels.collect{"'"+it+"'"}.join(',')
				log.info "channels:${channels}"
				List<String> dbrewards = db.queryForList("select reward from rewards where channel in (${channels})",String.class)
				log.info "rewards:${dbrewards}"
				jsonReply.account{rewards dbrewards}
				return Response.ok(jsonReply.toString()).build()
			break;
			case EligibilityOutput.CUSTOMER_INELIGIBLE.name():
				// 204 No Content (successfull result, but no rewards) 
				return Response.noContent().build()
			break;
		}
		//not recognized EligilibityService response
		return Response.serverError().build()
	}
	

}
