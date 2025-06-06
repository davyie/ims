What is the difference between the two repositories? 
- JPA extends PagingAndSortRepository which in turn externds crud. So JPA has additional functionality over crud. 

What is required to create JPArepository? 
- We need a working sql database. We need a repository which extends jpa or crud repository. We need an entity. Then if we want to implement our own mysql queries in jpa we have to specify it with @Query annotation and a sql-like query. 

What is an entity? 
- This is an object which reflects the sql database. We use the annotation @Entity. 

What does @Entity do? 
- This represents a table in relational database. Each instance of an Entity represents a row in the table. 
These rows are mapped using @Id, @GeneratedValue and @Column. 

What does @Column do? 
- It maps each class field to a column in the relational database. 

What is JPA? 
- This is an API for managing Persistent data in Java. It is essential for working with 
relational databases. 
It allows java developers to interact with sql databases without writing queries. 

What is the difference between JPA repository and Entity Manager? 
- JPA repository comes with a set of predefined methods to interact with database. These can also be extended by developers. 
Entity Manager requires developers to write raw SQL queries or use an API to interact with database. 

What is the difference between JPA and Hibernate? 
- JPA is a specification which means that it describes how to accomplish things. Hibernate is 
an implementation of the specification JPA. 

What does @Table do? 
- It gives the name for a JPA table. 

Can we use inheritance for JPA entities? 
- Yes, we can use an abstracted class to specify the top level fields and then use @MappedSuperClass annotation to make all entities inherit from it. 
But the child entities have to extend the parent. 

What is the definition of Cascading? 
- This is when an operation affects both and object and the related objects aswell. 

How can cascading cause issues? 
- The operations might affect objects which we don't wish to do. We might delete child objects. It can cause data change that is irreversible thus we lose data. 
This can also cause our application to not work as intended. 

What is the concept of Auditing? 
- This is the concept of tracking time of operations we perform. It essentially logs when things change and other stuff. 

How are Enums handled in JPA? 
- They are usually stored as numbers but we can also specify the kind by using @Enumerated(Type=<Type>) to specify how we want it to be stored. 

What are Projections? 
- This is when we fetch a subset of an object from database. This is usually done with DTO interface and using @Query annotation. 

Why are projections preferrable? 
- It fetches only necessary data which improves application performance. 
It essentially removes unnecessary database operations. 

What are @Index? 
- This is a performance enhancing feature which we specify which fields we can use to improve read operations from database. 
We do this by specifying the column we want to create index on. 

How are indices executed in JPA? 
- The idea is that JPA creates specific datastructures which enhance read operations. 

