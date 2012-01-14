package conf
import javax.annotation.PostConstruct

import org.apache.camel.CamelContext
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.jdbc.core.JdbcTemplate;

def environment=System.getProperty("environment")?:'development'
class SetupCamelRouting {
	@Autowired
	CamelContext camel
	class MyCamelBuilder extends org.apache.camel.language.groovy.GroovyRouteBuilder{
		public void configure() throws Exception{
			from('direct:reward').to('mock:result')
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
	context.'property-placeholder'(location:'classpath:conf/${environment}/config.properties')
	propertyPlaceholderConfigurer(PropertyPlaceholderConfigurer){
		location="classpath:conf/${environment}/config.properties"
	}
	camel.'camelContext'(id:'camel')
	setupCamelRouting(SetupCamelRouting)
	rewardService(com.gfrison.services.RewardService)
	jaxrs.'server'(id:'restService', address:'http://localhost:3000',
		staticSubresourceResolution:"true"){
	
		jaxrs.'serviceBeans'{
			ref(bean:'rewardService')
		}
	}
	dataSource(BasicDataSource){bean->
//		bean.destroy-method='close'
		driverClassName='${db.driver}'
		url='${db.url}'
		username='${db.username}'
		password='${db.password}'
	}
	db(JdbcTemplate){
		dataSource=ref('dataSource')
	}

}