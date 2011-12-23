package conf
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.jms.core.JmsTemplate

import com.gfrison.test.Prova;
def environment=System.getProperty("environment")
beans {
	xmlns util:"http://www.springframework.org/schema/util"
	xmlns context:"http://www.springframework.org/schema/context"
	xmlns jms:"http://www.springframework.org/schema/jms"
	xmlns jee:"http://www.springframework.org/schema/jee"
	xmlns aop:"http://www.springframework.org/schema/aop"
	xmlns lang:"http://www.springframework.org/schema/lang"
	xmlns task:"http://www.springframework.org/schema/task"
	log4jInitialization(MethodInvokingFactoryBean){
		targetClass="org.springframework.util.Log4jConfigurer"
		targetMethod="initLogging"
		arguments=[new java.lang.String("classpath:conf/${environment}/log4j.xml")]
	}
	context.'property-placeholder'('location':"classpath:conf/${environment}/config.properties")
	context.'component-scan' ('base-package':"com.covestor.test")
	task.executor (id:"taskExecutor", 'pool-size':"10")

}