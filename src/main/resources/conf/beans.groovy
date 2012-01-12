package conf
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer

def environment=System.getProperty("environment")?:'development'
// http://goo.gl/jv3YP Runtime Spring with the Beans DSL
beans {
	log4jInitialization(MethodInvokingFactoryBean){
		targetClass="org.springframework.util.Log4jConfigurer"
		targetMethod="initLogging"
		arguments=[new java.lang.String("classpath:conf/${environment}/log4j.xml")]
	}
	propertyPlaceholderConfigurer(PropertyPlaceholderConfigurer){
		location="classpath:conf/${environment}/config.properties"
	}


}