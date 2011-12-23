Proto App
=========

This is an archetype of any Java or Groovy written standalone application.

Gradle
------

Dependencies and build management is delegated to Gradle

If you want to run it simply type: `gradle exec`

* compile java -- `gradle compileJava` 

* compile groovy -- `gradle compileGroovy` 

* test -- `gradle test` 


Spring
------

The project makes use of Springframework through the [Grails based DSL](https://github.com/gfrison/proto-app/blob/master/src/main/resources/conf/beans.groovy).

It's a very concise way to handle Spring configuration. For more information (http://www.grails.org/doc/latest/guide/spring.html#springdsl).

  
Eclipse
-------

In order to get the project dependencies in Eclipse just prompt `gradle eclipse` 

