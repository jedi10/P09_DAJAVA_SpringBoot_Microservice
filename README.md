# P09_DAJAVA_SpringBoot_Microservice

## First Run

Build each project with Maven file _pom.xml_ to import all dependencies.
    
    mvn package
    mvn clean install

Install a MySQL/Maria DBB like [Xampp](https://www.apachefriends.org/fr/index.html). 

### BDD Configuration

* Configure the access to the database in *application.properties* file.
* Create two database: _mediscreen_ and _mediscreentest_.
* Execute Patient application once: on the first run, all tables will be build in mediscreen.
* Execute Patient test once: on the first run, all tables will be build in mediscreentest. 
    
## Resources
    
The project need _Java JDK 11_ or newer.
Open JDK is recommended: https://adoptopenjdk.net

The project use [Spring Boot 2.5.1](https://start.spring.io) 

### Dependencies 
     
    Spring Web (Tomcat),    
    Spring Validation,
    Spring Data JPA,
    Mysql-connector-java    
    Lombok,
    Swagger,
    Docker      	
    

## Application End-Points

All application End-Points can be find on one public html page (swagger):

    http://localhost:8081/swagger-ui.html

## Dockerization

### for MySQL/Maria DBB

    docker run -p 3306:3306 mariadb
    
[source](https://serverfault.com/questions/1019091/how-to-import-data-into-a-mariadb-instance-running-in-a-docker-container)