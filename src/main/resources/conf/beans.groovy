package conf
import javax.annotation.PostConstruct

import org.apache.camel.CamelContext
import org.apache.camel.Processor
import org.apache.commons.dbcp.BasicDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.jdbc.core.JdbcTemplate

import com.gfrison.services.InitDevelopmentDatabase

def environment=System.getProperty("environment")?:'development'
class SetupCamelRouting {
	def eligibilityServiceEndpoint
	@Autowired
	CamelContext camel
	class MyCamelBuilder extends org.apache.camel.language.groovy.GroovyRouteBuilder{
		public void configure() throws Exception{
			from('direct:rewards').to(eligibilityServiceEndpoint)
			
			//just for development. (gradle run) 
			//fake ElibiltyService which response always CUSTOMER_ELIGIBLE
			def alwaysEligible = {exchange -> if(exchange){exchange.getOut().setBody('CUSTOMER_ELIGIBLE')}} as Processor
			from('direct:eligibility').process(alwaysEligible)
		}
	}
	@PostConstruct
	void init(){
		camel.addRoutes(new MyCamelBuilder())
	}
}
// http://goo.gl/jv3YP Runtime Spring with the Beans DSL
beans {
	xmlns jaxrs:"http://cxf.apache.org/jaxrs"
	xmlns camel:"http://camel.apache.org/schema/spring"
	xmlns context:"http://www.springframework.org/schema/context"
	context.'annotation-config'()
	log4jInitialization(MethodInvokingFactoryBean){
		targetClass="org.springframework.util.Log4jConfigurer"
		targetMethod="initLogging"
		arguments=[new java.lang.String("classpath:conf/${environment}/log4j.xml")]
	}
	//'gradle run' load 'development' configuration
	context.'property-placeholder'(location:'classpath:conf/${environment}/config.properties')
	camel.'camelContext'(id:'camel'){
		camel.'template'(id:'eligibility', defaultEndpoint:'direct:rewards')
	}
	setupCamelRouting(SetupCamelRouting){
		eligibilityServiceEndpoint='${eligibilityServiceEndpoint}'
	}
	
	//real RewardService
	rewardService(com.gfrison.services.RewardService)
	
	//rest service container (CXF)
	jaxrs.'server'(id:'restService', address:'http://${http.host}:${http.port}',
		staticSubresourceResolution:"true"){
	
		jaxrs.'serviceBeans'{
			ref(bean:'rewardService')
		}
	}
		
	//channel's rewards has to be stored in a  database. For testing
	// and for demo it will use HsqlDb (in-memory db)
	dataSource(BasicDataSource){
		driverClassName='${db.driver}'
		url='${db.url}'
		username='${db.username}'
		password='${db.password}'
	}
	db(JdbcTemplate){
		dataSource=ref('dataSource')
	}
	initHsqlDb(InitDevelopmentDatabase){
		performInit = (environment.equals('development'))?true:false
	}

}