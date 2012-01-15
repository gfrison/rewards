package com.gfrison

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * 
 * @author Giancarlo Frison <giancarlo@gfrison.com>
 *
 */
class GStarter { 

	static main(args) {
        ClassPathXmlApplicationContext cx = null;
        try {
			String appname=System.getProperty("app.name")?:'',appversion=System.getProperty("app.version")?:''
			def bb = new grails.spring.BeanBuilder()
			bb.loadBeans("conf/beans.groovy")
			bb.activate()
			ApplicationContext appContext = bb.createApplicationContext()
	
            final Logger log = Logger.getLogger("starter");
            final GrailsApplicationContext finalCx = appContext;
            Runtime.getRuntime().addShutdownHook(new Thread("shutdown") {
                @Override
                public void run() {
                    log.info("shutdown gracefully...");
                    finalCx.publishEvent(new ContextClosedEvent(finalCx));
                    finalCx.close();
                }
            });
			log.info 'environment:'+System.getProperty('environment')?:'development'
            log.info(appname + " " + appversion + " open for e-business");

        } catch (Throwable e) {
            e.printStackTrace();
            Logger log = Logger.getRootLogger();
            log.error("error during startup", e);
			new Killer();
        }
	}
}
