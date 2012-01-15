/**
 * 
 */
package com.gfrison.services

import groovy.util.logging.Slf4j;

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author Giancarlo Frison <giancarlo@gfrison.com>
 *
 */
@Slf4j
class InitDevelopmentDatabase {

	boolean performInit = false

	@Autowired
	JdbcTemplate db

	@PostConstruct
	void initHSQLDB(){
		if(performInit){
			log.info 'creating hsql database and fill records in it'
			Thread.sleep(1000)
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
		}
	}
}
