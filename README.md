Proto App
=========

This is an archetype of any Java or Groovy standalone application based on Springframework and Gradle.

Gradle
------

Dependencies and build management is delegated to Gradle

If you want to run it simply type: `gradle run`. Other tasks:

* compile java -- `gradle compileJava` 

* compile groovy -- `gradle compileGroovy` 

* test -- `gradle test` 

* run -- `gradle run`

* create launch script  -- `gradle installApp`

* create distribution (zip)   -- `gradle distZip` it creates a zip file according to the build.gradle's projectName and version


Spring
------

The project makes use of Springframework through the [Grails based DSL](https://github.com/gfrison/proto-app/blob/master/src/main/resources/conf/beans.groovy).

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






