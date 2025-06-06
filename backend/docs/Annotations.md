# Annotations 
## SpringBootApplication
What are the three problems this annotation solves? 
- It solves the problem of using `component scan`, `auto configuration` and define extra configuration on their application classes. 

Which three annotations are added in @SpringBootApplication? 
- @ComponentScan, @EnableAutoConfiguration and @Configuration. 

What does @ComponentScan? 
- This annotation scans components within the common parent package. This is usually used in combination with @Configuration. 

How does @ComponentScane scan beans? 
- We have to specify it in the configuration where to scan the beans. This includes in multi module maven project aswell. 

What is a Filter in terms of @ComponentScan? 
- We can configure the Scan by using filter, i.e., tell Spring which beans to include and exclude.

What does @EnableAutoConfiguration? 
- It enables Spring AutoConfiguration 

What does @Configuration? 
- It allows registering of extra beans in the context or import additional configuration classes.

## @AutoConfiguration
What is AutoConfiguration mechanism? 
- This auto-configures a spring application based on the jar dependencies we have declared. 

## @Configuration 
What is @Configuration? 
- This is a class level annotation indicating the source of bean definitions. 

How do we define Beans? 
- This is done with the @Bean method annotation. 
Invocation of Bean methods on @Configuration class can be used to defined inter-bean dependencies. 

What happens if we invoke @Bean method twice in a @Configuration class? 
- The magic happens because all @Configuration classes subclass with CGLIB. This means 
it checks for cached beans before it calls its parent method. This is also referred to beans having `singleton` scope. 

## @Aspect 
What is an aspect? 
- It is a Bean with additional functionality such as `pointcut` `advice` and `introduction`. 

What is pointcut? 
- It determines join points of interests and enables us to control when advice runs. 

What is a join point? 
- It is a point in the control flow of a program where the flow can arrive from two different paths. 

What is pointcut designator? 
- This is a marker for when to run advice. 
But also defines the join points so we can run different methods.

What is advice? 
- This is a piece of code which gets invoked depending on the annotation. 
We can invoke code before, around and after. But also after returning and we can pick up the return value. 
We can also do after throwing advice which handles exceptions. 

What is the requirement for Around advice? 
- Return type is Object. The parameter has to be ProceedingJoinPoint. And we have to invoke proceed() somwehere in the advice. 

Can we pass arguments to Advice?
- Yes, we do this by adding args(<parameter name>, ..) in the Advice. 

What are Introductions? 
- This enables an aspect declare that advised objects implement an interface and provide implementation of that interface on behalf of those objects. 
So it is a way to inject code. 

What are good pieces of code to have as Aspect? 
- That span across the application, .e.g, Logging, Monitoring, RateLimit and ConcurrentRetires. 

What is @ConfigurationProperties? 
- This is a way to inject values into a @Configuration-class.
- This allows us to perform validation aswell. 