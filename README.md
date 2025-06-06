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


