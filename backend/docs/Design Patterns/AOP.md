# Aspect oriented programming 
What is the philosophy of AOP? 
- The philopsophy of AOP is to split a program into aspects. An aspect is a functionality in a program. For example, logging or configuration. These aspects should be split out from the actual business logic. 

What are the two ways of implementing AOP in Spring? 
- Schema based or annotation based.

What is join points? 
- A join point represents a method execution. 

What is an advice? 
- An advice is an action taken at a particular join point. 
It is modelled as an interceptor and maintain a chain of interceptors around the join point. 

What is pointcut? 
- It is a predicate which is a statement that is either true or false. It is used to run 

What is Introduction in terms of AOP? 
- These are additional methods or fields on behalf of a type. It does this by using Java Interfaces. 

What is target object? 
- These are objects advised by one or more aspects. It is also referred to as `advised object`. 

What is AOP proxy? 
- This is an object created by the AOP framework in order to implement the aspect contracts. 

What is weaving? 
- This is the concept of linking aspects with other application types or objects to create an advised object. 

What is the difference between Spring AOP and AspectJ? 
- AspectJ is a standalone framework while Spring AOP is integrate into Spring IoC. Spring AOP is built on proxies. 

What is required to use AspectJ in a Spring project? 
- We have to add `aop` and `logging` dependency to our project. 

What are Dynamic pointcuts? 
- These are predicates which can change with method arguments. This means that they must be evaluated with every method invocation and the result cannot be cached as the arguments will vary. 

Give an example of dynamic pointcut.
- Control flow. 

What is the inceptor chain? 
- This is a chain of components to process a request or response in sequence. 

What can we use inceptors for? 
- We can use it in: 
* logging
* authentication 
* caching 

# Logging
What is logging? 
- This is the concept of tracking activity inside our application. 

How is logging achieved in Spring? 
- We apply AOP pattern and set up an aspect class. Then we have annotations which specify which advice to execute before which pointcut. 

What annotation do we use? 
- We use `@Before`, `@Around` or `@After`. The `@Before` annotation runs our advice before the execution of the specify method in our annotation. 

How are the annotations set up? 
- Example: We have `@Before()` where in the parentheses we have the path to our method(s). We can use asterisk as wildcard and we also have to match the method signature. 
`Before("execution(* coms.ims.crochet_v2.Application.Controller.*(..))")`
This annotation will match all methods inside of Controller class with any method signature. So the string is `<package>.<package>.<package>....`. 

What is the notation for matching all method signatures? 
- `(..)`

Can we match other methods aswell? 
- Yes, we can match annotations and use `within` keyword to match the annotation in specific class. 

Where does the AOP pattern come in? 
- We use annotations to mark the methods we want to apply logging on. In this way we don't need to add any code inside of the methods we want to apply logging. Instead we use annotations and method signatures.  

