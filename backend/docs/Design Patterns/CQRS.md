# CQRS 

How do we connect handler to controller? 
- For queries we use request parameters and not path variable. The query handler is run in the 
endpoint route method. The handler receives an instance of Query. Then the handler 
checks what type of query it receives and run the correct method. 
The request parameters are passed into Query instance, contructor style. 

