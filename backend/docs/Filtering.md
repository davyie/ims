# Filtering 

What is filtering? 
- This is conecpt of intercepting HTTP requests and responses in a Web application. 

What part is Filter is of? 
- It is part of Servlet mechanism. 

What are filtering used for? 
- It is used for processing requests before reaching the web app and process responses before sending it to client. 

What are the two ways of creating filters? 
- using Spring DI mechanism with bean
- using @WebFilter annotation that does not utilize DI.

## Creating Filter with Bean 
How do we create filter? 
First way: 
- Create configuration. Add RegistrationBean. Use @Configuration and @Bean
- Create CustomFilter which implements Filter. 
- Add the project as dependency in Main package. 

Second way: 
- Create CustomFilter. Implement Filter. Use @WebFilter class annotation. 
- Don't know how to use. 

What is a Servlet? 
- A servlet is a class which extends the capabilities of a server by being responsible for request-response model. 

How do we add multiple filters? 
- We just add more registration beans to the Configuration. 

