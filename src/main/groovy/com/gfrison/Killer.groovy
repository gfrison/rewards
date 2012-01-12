package com.gfrison 
import groovy.util.logging.Slf4j
;

/**
 * @author Giancarlo Frison <giancarlo@gfrison.com>
 */
@Slf4j
public class Killer extends Thread {


    int status = 0;
    
    public Killer() {
        setName("killer thread");
        start();
    }
    
    public Killer(int status) {
    	this.status=status;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        }
        catch (Throwable e) {
            log.error("run", e);
        }
        log.info("shutdown server");
        System.exit(status);
    }
}