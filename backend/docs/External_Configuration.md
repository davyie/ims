# External Configuration 

Good discussion about external configuration: https://stackoverflow.com/questions/23138494/spring-boot-application-properties-maven-multi-module-projects 


What is the purpose of application.properties? 
- It is a main configuration file for Spring Boot Application. It is used to defined application settings such as database connections, server ports or feature toggles. 

Where is application.properties located? 
- It is located in `src/main/resource`. 

What is .env? 
- This is also configuration of Spring boot application. It defines environment variables that can be accessed by the application. It is usually at the root of the project. 

Does the configuration order matter? 
- Yes, parameters has the highest priority. Followed by .env and then application.properties and last code. 

What are best practices? 
- Use .env to store sensitive information. Use spring-profiles for different environments. 
Keep .env from version control by including it in .gitignore. 

How does spring load in application.properties? 
- It loads in the values from the highest level. So if a child uses application.properties value and it is declared in parent's then the child can use it. 
Oke... The values are loaded only for the JAR modules. So, once the JAR is running it loads in the `application.properties`. 

How do we include *.properties files from Main? 
- We can use `@PropertySource` to include it. 
Just add this `@PropertySource("classpath:custom-config.properties")` in `@SpringApplication` file. 

What does `@PropertySource` do? 
- This provides properties file into spring environment. We usually use it together with `@Configuration`. 

What does `@Configuration` do? 
- This is a class which configures the Spring application context. It can set up variables, initialize beans into the spring application context and more. 

How do we load in the .env? 
- we can do it multiple ways. We can either `source` it in the terminal and run it in the container. We can also to it programmatically through Main application and dotenv dependency. 
We should probably do this in the configuration file of the main application. 

