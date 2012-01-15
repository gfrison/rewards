Rewards
=========

This is the demo application available at: https://github.com/gfrison/rewards 

It leverages on:

* Groovy

* Gradle

* Springframework

* Camel

* CXF

Open a Linux console and go to the project home.

Type `gradle test` you will test it.

Type `gradle run` you will run it (with development setup). 
After that you may test the up and running application typing
`curl -v http://localhost:8080/rewards/account/234234 -X GET -d '{"portfolio":{"channels":["SPORTS"]}}'` 

Type `gradle distZip` you will obtain a complete deliverable zip file which contains an executable launcher

Configuration
-------------

Available on src/main/resources

RewardService: src/main/groovy/com/gfrison/services/RewardService.groovy 


Gradle
------

Dependencies and build management is delegated to Gradle.

Install it from http://gradle.org not from `apt-get install gradle`. More recent version, more better  

If you want to run it simply type: `gradle run`. Other tasks:

* compile java -- `gradle compileJava` 

* compile groovy -- `gradle compileGroovy` 

* test -- `gradle test` 

* run -- `gradle run`

* create launch script  -- `gradle installApp`

* create distribution (zip)   -- `gradle distZip` it creates a zip file according to the build.gradle's projectName and version


Spring
------

The project makes use of Springframework through the [Grails based DSL](https://github.com/gfrison/rewards/blob/master/src/main/resources/conf/beans.groovy).

It's a very concise way to handle Spring configuration. For more information (http://www.grails.org/doc/latest/guide/spring.html#springdsl).

  
Eclipse
-------

In order to get the project dependencies in Eclipse just prompt `gradle eclipse` 


Environments
------------

It's possibile to customize configuration for any given environment such as development, integration, 
production (or whatever you want define).

Add the folder `src/main/resources/conf/{environment name}/`.

Then set the proper environment's placeholer `config.properties` and `log4j.xml`.

When you run the application from start script you have to set the system variable ex:

`export APP_OPTS="-Denvironment=<development/integration/production>"`






