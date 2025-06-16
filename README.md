# About 
This project is about creating an Inventory Management System (ims) for small businesses. 
A business owner should be able to handle her warehouse inventory and extract inventory to separate markets. 
During markets, she should be able to keep track of market inventory to see how much she has sold throughout a market. 

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

## Application Requirements (Backend)

### Market 

- [x] Open Market 
- [x] Add Item to Market 
- [] Sync Market inventory to Warehouse Inventory 
- [] Update market item 
- [x] Delete market item 
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
- [x] Handle Exceptions Globally 


### Logging
- [x] Add Logging Aspect 

### Pricing 
- [] Open 


## Application requirement (Frontend)

### Market 

- [] Open Market 
- [] Add Item to Market 
- [] Update market item 
- [] Delete market item 
- [] Decrement market item 
- [] Increment market item 

### Warehouse 
- [] Open Warehouse 
- [] Add Item to Warehouse
- [] Decrement inventory 
- [] Increment inventory
- [] 


