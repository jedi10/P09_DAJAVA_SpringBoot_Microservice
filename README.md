# P09_DAJAVA_SpringBoot_Microservice

## First Run

Build each project with Maven file _pom.xml_ to import all dependencies.
    
    mvn package
    mvn clean install

Install a MySQL/Maria DBB like [Xampp](https://www.apachefriends.org/fr/index.html). 

### BDD Configuration (for Patient Microservice)

* Configure the access to the database in Patient *application.properties* file.
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
    Docker,
    Thymeleaf      	

## Dockerization

  * Install docker

  * Build all projects with maven (For Patient project, you need to run before MySQL database because Tests ask for it.)

        mvn clean package
   
  * Open a console in UI project and type: 
  
        docker compose up -d (main instruction for project dockerization)
     * -d run the container in detached mode = in the background

All the process of Dockerization is made by a _DockerCompose_ File created in UI project
 
 You can also use these command:
    
    docker-compose --version  
    docker-compose config (for docker-compose file validation)    
    docker compose ps -a
    docker compose logs -f --tail 5  (show docker container logs)
    docker compose <stop || start>
    docker compose down  (resource destruction)
    docker rmi -f patient
    docker rmi -f ui
      

### for MySQL/Maria DBB

A _DockerCompose_ File made a container with MariaDB Database.    

if you just want to see a container with MariaDB:

    docker run -p 3306:3306 mariadb
    
[source](https://serverfault.com/questions/1019091/how-to-import-data-into-a-mariadb-instance-running-in-a-docker-container)

## Application End-Points

### Documentation

End-Points Api Documentation can be find on public html page (swagger):

    http://localhost:8081/swagger-ui.html (for Patient Microservice)
    http://localhost:8091/swagger-ui.html (for UI Project)
    
### UI Project
Access to the all application go on UI Project which shows all data and interact with all other microservices.

   http://localhost:8091/patient/list

For developers, a local mode to test thymeleaf pages can be activated in PatientControler class.
