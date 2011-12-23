Proto App
=========

This is an archetype of any Java or Groovy written standalone application.

Gradle
------

Dependencies and build management is delegated to Gradle

If you want to run it simply type: `gradle exec`
*[compile java] -- `gradle compileJava` 
*[compile groovy] -- `gradle compileGroovy` 
*[test] -- `gradle test` 

Spring
------

The project makes use of Springframework through the Grails based DSL located in (src/main/resources/conf/beans.groovy).
It's a very concise way to handle Spring configuration (http://www.grails.org/doc/latest/guide/spring.html#springdsl)
  

