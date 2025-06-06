# Java

## JVM
What is classpath? 
- This is a parameter in JVM or compiler that specifies the location of user-defined classes and packages.
It is either set on command line or through environment variable.

## Access modifiers 
What is the difference between protected and private? 
- `private` modifier allow code declared in the same class to access. `protected` code in the same class or the derived classes can access this variable. 

## try & catch & throw 
The `try` statement allows you to define a block of code to be tested for errors while it is being executed. The `catch` statement allows us to define a block of code to be executed, if an error occurs in the try block. 
`throw` statement allows us to create custom exceptions and errors. 

## FileWriter
Why do we need to flush? 
- To minimize data leakage and ensure that all data is being written to file. 
flush() makes sure that any buffered data is written to disk. 
close() it flushes the data and closes the stream. To open it again we have to call another read or write object. 

## Error & Exception 
What is the difference between Errors and Exceptions? 
- They represent different types of problems that can occur during the execution of a program. Errors are problems that occur outside the control of the program, e.g., running out of memory or system crash. 
Exceptions are errors that are handled within the program. Usually with errors we terminate the program and log the error while exceptions are handled and the program continue running. 

What are checked and unchecked exceptions respectively? 
- Checked refers to the compiler checking the exceptions. Unchecked are exceptions which are not checked by the compiler.  

How do we write exceptions? 
- All exceptions must inherit throwable. 
To implement a checked exception we extend `Exception` class.
To implement an unchecked exception we extend `RunTimeException` class. 

What is the difference between `throw` and `throws`? 
- `throws` is declared in the method declaration while `throw` is declared when there can be an exception. 

How do we use `throw` and `throws`? 
- `throws` is used when we want to postpone an exception. `throw` is used when we want to throw an exception. 

Will the program exit if the exception is not caught? 
- Yes. The errornous code have to be inside of try/catch block such that the exceptions thrown are caught and handled. 

## Object mapper
What is ObjectMapper? 
- It is a class from Jackson which serializes objects into JSON. 

What is serialize? 
- This is the process of translating a data structure or object state into a format that can be stored. 

What is deserialize? 
- This is the process of translating a format into objects. 

What problem does this solve? 
- It solves the problem of storing our data structures and objects long term in a format suitable for storing.

## Database 
What is a DataSource? 
- This is a connection set up to a database.


## Classloaders
What are ClassLoaders? 
- These are responsible for loading in classes in JVM (Java Virtual Machine) during runtime.
