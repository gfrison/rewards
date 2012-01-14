/**
 * 
 */
package com.gfrison

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.ws.rs.core.Response

import org.apache.camel.EndpointInject
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExpressionBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.commons.dbcp.BasicDataSource
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.jdbc.core.JdbcTemplate

import com.gfrison.services.RewardService

/**
 * @author gfrison
 *
 */
class RewardServiceTest extends CamelTestSupport{

	@EndpointInject(uri="mock:eligibility")
	MockEndpoint eligibility

	@Produce(uri = "direct:rewards")
	ProducerTemplate template;

	static JdbcTemplate db
	RewardService rewardService
	def json

	@BeforeClass
	static void init(){
		def datasource = new BasicDataSource()
		datasource.setDriverClassName("org.hsqldb.jdbcDriver")
		datasource.setUrl("jdbc:hsqldb:mem:.")
		datasource.setUsername("sa")
		datasource.setPassword("")
		db = new JdbcTemplate(datasource)
		db.execute("""
              create table rewards (
				channel varchar (50) not null, 
				reward varchar(100) null, 
				primary key(channel)
			  )
			""")
		db.execute("insert into rewards values ('SPORTS','CHAMPIONS_LEAGUE_FINAL_TICKET')")
		db.execute("insert into rewards values ('MUSIC','KARAOKE_PRO_MICROPHONE')")
		db.execute("insert into rewards values ('MOVIES','PIRATES_OF_THE_CARIBBEAN_COLLECTION')")
		assertEquals('CHAMPIONS_LEAGUE_FINAL_TICKET', db.queryForObject("select reward from rewards where channel='SPORTS'",String.class))
	}

	@Before
	void initInstance(){
		rewardService = new RewardService(producer:template, db:db)
		json = new JsonBuilder()
		json.portfolio {
			channels(['SPORTS'])
		}
		BasicConfigurator.configure()
		Logger.getRootLogger().setLevel(Level.INFO)
	}


	@Test
	void testInvokingEligibilityWithCorrectAccounID(){
		eligibility.returnReplyBody(ExpressionBuilder.simpleExpression(RewardService.EligibilityOutput.CUSTOMER_ELIGIBLE.name()))
		String expected = "account-12345"
		eligibility.expectedBodiesReceived(expected)
		rewardService.getRewards(expected, json.toString())
		eligibility.assertIsSatisfied()
	}

	@Test
	void testEligibility(){
		eligibility.returnReplyBody(ExpressionBuilder.simpleExpression(RewardService.EligibilityOutput.CUSTOMER_ELIGIBLE.name()))
		Response res = rewardService.getRewards("account-12345", json.toString())
		assertEquals(Response.ok().build().getStatus(), res.getStatus())
		def slurper = new JsonSlurper()
		def json = slurper.parseText(res.getEntity())
		assertEquals('CHAMPIONS_LEAGUE_FINAL_TICKET',json.account.rewards[0])
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder(){
			public void configure(){
				from("direct:rewards").to("mock:eligibility");
			}
		}
	}



}
