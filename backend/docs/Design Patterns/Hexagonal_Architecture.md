# Hexagonal Architecture
What is hexagonal architecture? 
- It is a pattern that revolves around designing applications around domain logic. 

What are the layers of hexagonal architecture? 
- Application layer, Domain layer and infrastructure layer. 

What is application layer? 
- This contains user interfaces, rest controllers, json serialization libraries etc. 

What is Domain layer? 
- This layer contains all the code which relate to business logic. It should be isolated from application layer and infrastructure layer. 

What is infrastructure layer? 
- It contains code to make the application to work, e.g., databases, configurations etc. 