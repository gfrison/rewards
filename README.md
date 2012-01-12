Proto App
=========

This is an archetype of any Java or Groovy standalone application.

Gradle
------

Dependencies and build management is delegated to Gradle

If you want to run it simply type: `gradle exec`

* compile java -- `gradle compileJava` 

* compile groovy -- `gradle compileGroovy` 

* test -- `gradle test` 

* run -- `gradle run`

* create launch script  -- `gradle installApp`

* create distribution (tar.gz)   -- `gradle distZip`


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
production -- or whatever you want define -- 
adding the folder `src/main/resources/conf/{environment name}/`,
then compiling the proper placeholer `config.properties` for the environment specific variables and
`log4j.xml` for configuring logging.

For selecting the environment during the execution set system property `gradle exec -Denvironment=<environment>` or update build.gradle file.



