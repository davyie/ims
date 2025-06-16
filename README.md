Research 
- [x] @SpringApplication annotation 
- [x] @ComponentScan annotation
- [x] @Aspect annotation
- [x] subpackages 
- [] class loaders
- [x] ApplicationContext
- [x] @ConfigurationProperties
Study how configuration works in spring.

# How build the project

## Software 
Java 17 
Docker desktop or engine
unix based terminal or powershell

## Build 
run `mvn clean install`
run `docker build . -t application:1.0.0`
if docker containers are running: `docker compose down -v` 
else `docker compose up -d`

Or you can run `./build.sh` from the terminal. 

## Application Requirements

### Market 

- [x] Open Market 
- [] Add Item to Market 
- [] Sync Market inventory to Warehouse Inventory 
- [] Update market item 
- [] Delete market item 
- [] Decrement market item 
- [] Increment market item 

### Warehouse 
- [x] Open Warehouse 
- [x] Add Item to Warehouse
- [x] Decrement inventory 
- [] Increment inventory
- [x] Exception handling for stock under 0. Handling when trying to add item to market which has insufficient quantity. 
- [] 

### Exception handling 
- [] Add IllegalStateException Aspect 

### Logging
- [] Add Logging Aspect 

### Pricing 
- [] Open Warehouse 




