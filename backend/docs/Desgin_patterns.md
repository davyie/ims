# Design Patterns
What are design patterns? 
- These frequently occuring patterns in software engineering which solve various problems. 

What are the three types of design patterns? 
- Creational, Structural, Behavioural. 

What problem does creational solve? 
- The main problem within creation is that constructors cannot do everything. 
- Complex object creation, i.e., multiple steps or complex logic, like setting many parameters and initialising dependencies. Using many parameters with constructor can be cluttering and hard to maintain code. 
- Flexibility in creating instances of different classes that share a common interface. 
- Object creation logic might be scattered throughout the codebase, leading to duplication and making changes to the creation process difficult. 
- Managing lifecycle of objects is often necessary for resource management and performance reasons. 
- Avoid tight coupling. Direct instantiation of objects can lead to tight coupling. 
- When client code doesn't need to know about the specific class of an object to be created. 

What is telescoping constructor? 
- This is when we have multiple constructors with various numbers of parameters. This can become cluttered real quick. 

What problem does Builder solve? 
- It solves the problem of having multiple constructors but also having to maintain many different subclasses from a parent class. 





# Logging 
What is SLF4J? 
- This is a logging facade for different logging framework, e.g., log4j or logback. 