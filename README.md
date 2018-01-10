## lagom-header-service

### Pre-requisites 
    jdk8
    maven
    
### Running the service
    mvn lagom:runAll
    
#### ServiceCall
    Method type: GET
    URL example: http://localhost:9000/read/data/serviceCall?color=RED
    RequestHeader: Not Needed, passed programmatically

#### ServerServiceCall
    Method type: GET
    URL example: http://localhost:9000/read/data/serverServiceCall?color=RED
    RequestHeader: Not Needed, passed programmatically

#### HeaderServiceCall
    Method type: GET
    URL example: http://localhost:9000/read/data/headerServiceCall?color=RED
    RequestHeader: Needed dummy authorization key value pair (authorization_key, 7208015cfb39d49fd39b1339f4627281)
    
### Compiling the service
    mvn clean compile
    
### Running Unit Test Cases
    mvn clean test

### Running Checkstyle
    mvn verify
    Check -- */target/checkstyle/checkstyle-output.xml files for results
