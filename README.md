# P09_DAJAVA_SpringBoot_Microservice

## First Run (Step 1)

Build each project with Maven file _pom.xml_ to import all dependencies.
    
    mvn package
    mvn clean install

Install a MySQL/Maria DBB like [Xampp](https://www.apachefriends.org/fr/index.html). 

### BDD Configuration (for Patient Microservice)

* Configure the access to the database in Patient *application.properties* file.
* Create database: _mediscreentest_
* Execute Patient test once: on the first run, all tables will be build in _mediscreentest_. 
    
## Resources
    
The project need _Java JDK 11_ or newer.
Open JDK is recommended: https://adoptopenjdk.net

The project use [Spring Boot 2.5.1](https://start.spring.io) 

### Dependencies 
     
    Spring Web (Tomcat),    
    Spring Validation,
    Spring Data JPA,
    Mysql-connector-java
    Spring-boot-starter-data-mongodb    
    Lombok,
    Swagger,
    Docker,
    Thymeleaf      	

## Dockerization (Step 2)

  * Install docker

  * Build all projects with maven (For Patient project, you need to run before MySQL database because Tests ask for it.)

        mvn clean package
   
  * Open a console in UI project and type: 
  
        docker compose up -d (main instruction for project dockerization)
     * -d run the container in detached mode = in the background
     
  * Go on Mediscreen Application with a web browser:

        http://localhost:8091/  (Home Page) 

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
      

### Application Architecture 

![Application Package Diagram](https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/assets/package_diagram.png)

### for MySQL/Maria DBB

A _DockerCompose_ File made a container with MariaDB Database.    

On project, MariabDb container can be find on this URL:

    http://localhost:3306

if you just want to see a container with MariaDB:

    docker run -p 3306:3306 mariadb
    
[source](https://serverfault.com/questions/1019091/how-to-import-data-into-a-mariadb-instance-running-in-a-docker-container)

### for MongoDB

A _DockerCompose_ File made a container with MongoDB Database and a container for Docker Express in order to check data or save them (export).
    
On project, you can find MongoDb Database on this URL:

    http://localhost:27017
    
You can find Mongo Express on this URL:
   
    http://localhost:8081
    
    
### UI Project: main page for Mediscreen application
Central Project is UI Project which shows all data and interact with all microservices.
    
    http://localhost:8091/  (Home Page) 
    http://localhost:8091/patient/list (Main Page)
    
For developers, a local mode to test thymeleaf pages can be activated in UI PatientController and UI NoteController class.
    
Please note that in the actual version of the application, to obtain the patient risk, you will have to use this URL:

    http://localhost:8061/access/familyName?familyName=dupond
    http://localhost:8061/access/id?patId=1
    **********************************
    Patient: Paul Dupond (age 15) diabetes assessment is: No Risk
    
#### List of Risk Factor used by application

    H??moglobine A1C
    Microalbumine
    Taille
    Poids
    Fumeur
    Anormal
    Cholest??rol
    Vertige
    Rechute
    R??action
    Anticorps
    
## Application End-Points

### Patient Microservice End-Points
Patient microservice manages all patient operations

[![Swagger Capture for Patient](https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/assets/swaggerPatient.png)][1]

### Note Microservice End-Points
Note microservice manages all patient note operations

[![Swagger Capture for Note](https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/assets/swaggerNote.png)][2]

### UI End-Points for Thymeleaf pages
UI project show Web Pages for all Patient and Note CRUD Operation

[![Swagger Capture for UI](https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/assets/swaggerUI.png)][3]

### Risk Microservice End-Points
Risk microservice determine the patient diabete risk Level by analysing Patient Notes with risk factor keywords

[![Swagger Capture for Risk](https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/assets/swaggerRisk.png)][4]


### Swagger URL

On project, End-Points Api Documentation can be find on public html page (swagger):

    http://localhost:8091/swagger-ui.html (for UI Project)
    http://localhost:8085/swagger-ui.html (for Patient Microservice)
    http://localhost:8071/swagger-ui.html (for Note Microservice)
    http://localhost:8061/swagger-ui.html (for Risk Microservice)     
    


[1]: https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/swagger/PatientMicroservice.pdf
[2]: https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/swagger/NoteMicroservice.pdf
[3]: https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/swagger/mediscreenUI.pdf
[4]: https://jedi10.github.io/P09_DAJAVA_SpringBoot_Microservice/swagger/RiskMicroservice.pdf