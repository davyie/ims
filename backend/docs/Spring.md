What creates the jar file? 
- It is the dependency spring-boot-starter. 

# Spring Framework 

## Annotations 
`@Component` - `@ComponentScan` in `@SpringBootApplication` will search in our package for this annotation and initialize the classes and keep them for dependency injection when needed. 
`@Bean` - This is used in the config file which is to setup the Spring Application context. The annotation is used together with functions. 

What is the difference between `@Component` and `@Bean`? 
- `@Bean` allow us to initialize objects with a specific constructor unlike `@Component` which uses the default constructor. `@Component` uses more automatic detection while `@Bean` is about manual bean registration with custom logic. 

## Spring application context 
This is the environment which supports the `Inversion of control`. We can set up core components `@Bean` to this environment. 

## Inversion of control 
This is the concept of framework controlling our classes and objects instead the programmer uses the framework or library classes. 

Domain Driven Design
Behaviour Driven Design 

What is an embedded server? 
- This is a technology which allow us to combine both code and server into a single executable. Example of this is Tomcat. The JAR file contains a Tomcat server with compiled code. This allow us to run our application on a server without deploying an external server. 