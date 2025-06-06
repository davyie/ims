# Learning Outcomes 

## Data Transfer Objects

What are Data Transfer Objects? 
- These are objects which are mainly used to transfer data between servers. These can be used to hide information and add information during transit. 

What problem does DTO solve? 
- DTO solves the problem of hiding the backend implementation of the system. 

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


