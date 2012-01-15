package com.gfrison
import org.apache.camel.Exchange;


import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import javax.ws.rs.core.Response

import org.apache.camel.EndpointInject
import org.apache.camel.Processor
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

import com.gfrison.services.InitDevelopmentDatabase;
import com.gfrison.services.RewardService

/**
 * @author giancarlo@gfrison.com <Giancarlo Frison>
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
		def initHSQLDB = new InitDevelopmentDatabase(performInit:true,db:db)
		initHSQLDB.initHSQLDB() 
		assertEquals('CHAMPIONS_LEAGUE_FINAL_TICKET', db.queryForObject("select reward from rewards where channel='SPORTS'",String.class))
	}

	@Before
	void initInstance(){
		rewardService = new RewardService(eligibility:template, db:db)
		json = new JsonBuilder()
		json.portfolio {
			channels(['SPORTS'])
		}
		BasicConfigurator.configure()
		Logger.getRootLogger().setLevel(Level.INFO)
	}


	/*
	 * test if EligibilityService receive correct input
	 */
	@Test
	void testInvokingEligibilityWithCorrectAccounID(){
		eligibility.returnReplyBody(ExpressionBuilder.simpleExpression(RewardService.EligibilityOutput.CUSTOMER_ELIGIBLE.name()))
		String expected = "account-12345"
		eligibility.expectedBodiesReceived(expected)
		rewardService.getRewards(expected, json.toString())
		eligibility.assertIsSatisfied()
	}

	/*
	 * test correct reward
	 */
	@Test
	void testEligibility(){
		eligibility.returnReplyBody(ExpressionBuilder.simpleExpression(RewardService.EligibilityOutput.CUSTOMER_ELIGIBLE.name()))
		Response res = rewardService.getRewards("account-12345", json.toString())
		assertEquals(Response.ok().build().getStatus(), res.getStatus())
		def slurper = new JsonSlurper()
		def json = slurper.parseText(res.getEntity())
		assertEquals('CHAMPIONS_LEAGUE_FINAL_TICKET',json.account.rewards[0])
	}

	@Test
	void testChannelWithoutReward(){
		json = new JsonBuilder()
		json.portfolio {
			channels(['KIDS']) //no rewards at this channel
		}
		eligibility.returnReplyBody(ExpressionBuilder.simpleExpression(RewardService.EligibilityOutput.CUSTOMER_ELIGIBLE.name()))
		Response res = rewardService.getRewards("account-12345", json.toString())
		assertEquals(Response.ok().build().getStatus(), res.getStatus())
		def slurper = new JsonSlurper()
		def json = slurper.parseText(res.getEntity())
		assertEquals(0,json.account.rewards.size())
	}

	/*
	 * test negative response from EligibilityService
	 */
	@Test
	void testIneligibility(){
		eligibility.returnReplyBody(ExpressionBuilder.simpleExpression(RewardService.EligibilityOutput.CUSTOMER_INELIGIBLE.name()))
		Response res = rewardService.getRewards("account-12345", json.toString())
		assertEquals(Response.noContent().build().getStatus(), res.getStatus())
	}

	/*
	 * test tech failure response from EligibilityService
	 */
	@Test
	void testTechnicalFailure(){
		eligibility.whenAnyExchangeReceived(new Processor(){
			void process(Exchange exchange) throws Exception{
				//unchecked exception for technical errors
				throw new RuntimeException("test technical failure")
			}
		});
		Response res = rewardService.getRewards("account-12345", json.toString())
		//503 Service Unavailable
		assertEquals(503, res.getStatus())
	}

	@Test
	void testInvalidAccount(){
		eligibility.whenAnyExchangeReceived(new Processor(){
			void process(Exchange exchange) throws Exception{
				throw new Exception("Invalid account number")
			}
		});
		Response res = rewardService.getRewards("account-12345", json.toString())
		//400 Bad Request
		assertEquals(400, res.getStatus())
		def slurper = new JsonSlurper()
		def json = slurper.parseText(res.getEntity())
		assertNotNull(json?.account?.error)
		
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
