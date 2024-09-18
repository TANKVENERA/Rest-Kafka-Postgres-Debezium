Kafka Debezium Integration
==========================

## Description

 Project presents a simple integration between kafka broker, kafka/debezium connectors and postgres with spring boot
 as the mediator. 
 Messages (client/transaction) are published to **client-topic** or **transaction-topic** via rest. Respective consumers write data to db.
 Debezium connector listens to db changes and produces change events. Kafka connector is used to calculate average transaction
 price during last day/hour/minute e. t. c. (see jdbc-client-config.json) and send result to corresponding topic. 
 Kafka Streams joins client and transaction streams and produces events to **fraud-client-topic** if total transaction price
 during last 2 minutes greater then 1000

![](../docker-run-example.jpg)

## Examples of usage:
 Go to **docker** directory and execute the following command:

`docker-compose up --detach zookeeper kafka db debezium-connect kafka-connect && docker-compose build && docker-compose up connectors-setup --force-recreate app`

### OR:
    
 1. `docker-compose up --detach zookeeper kafka db debezium-connect kafka-connect && docker-compose build && docker-compose up connectors-setup` - start infrastructure via docker
 2. `mvn spring-boot:run` - start main app locally

 **Go to Swagger**:
 [http://localhost:8080/rest--kafka--postgres--debezium/swagger-ui/index.html]()

 **Сreate client**:
 curl -X 'POST' \
 [http://localhost:8080/rest--kafka--postgres--debezium/client]() \
 -d '{
 "clientId": 10,
 "email": "some.email@gmail.com",
 "firstName": "someName",
 "lastName": "someSurName"
 }'

 **Сreate transactions**: (e.g. create 5 transactions)
 curl -X 'POST' \
 [http://localhost:8080/rest--kafka--postgres--debezium/transaction]() \

  -d '{
     "clientId":10,
     "bank": "Donald Duck Bank",
     "transactionType": "INCOME",
    "quantity": 1,
    "price": 500,
    "createdAt": null
}'

 **Check Results**
 ![](../app-run-example.jpg)

 **Stop Docker Containers**
 Execute the following command:

`docker-compose down`




