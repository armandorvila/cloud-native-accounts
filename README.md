# Cloud Native Accounts System

An example project to implement a cloud native version of an accounts system. 

Technology and concepts:

* Java 8
* Spring Framework 5
* Spring Webflux/Reactor
* Spring Data/MongoDB
* Microservice architecture
* Service discovery
* API gateway
* Docker and Docker compose
* Continuous Integration

The following concepts and implementation details are out of the scope so far:

* Security
* Event Sourcing and CQRS
* Service configuration 

### Domain

The system contains two main bounded contexts:

* Accounts
* Transactions

The accounts bounded context has account as main entity, and customer as a secondary entity, while the Transactions context in this domain has Transaction as main and only entity.

The following diagram shows the contexts, entities and relations: 

![Domain model](./assets/Cloud_Native_Accounts_Domain.png)

### Microservices

For this system, we are implementing one microserivce per bounded context, plus other non functional services in order to provide service discovery and an entry point for the system.

![Domain model](./assets/Cloud_Native_Accounts_Microservices.png)

### Endpoints

| Endpoint | Method | Public/Private | Description |
| ------------ | -------------- | ------- |
| `/accounts` | POST | Public | Creates a new account associated to the specified customer.  |
| `/accounts` | GET | Public | Retrieves all the accounts along with the transactions of each one. It implements a customerId filter (queryParameter) and limit/offset pagination.  |
| `/accounts/{accountId}` | GET | Public | Retrieves an specific account given the ID.  |
| `/accounts/{accountId}/transactions` | GET | Public | Retrieves all the transactions of a given account.  |
| `/transactions` | GET | Private | Retrieves all the transactions. It implements offset/size pagination.|
| `/transactions/{transactionId}` | GET | Private | Retrieves a specific transaction given the ID.  |

### Running

For the sake of simplicity, the microservices are built using in memory mongo db data bases. A docker compose has been provided to run the entire system:

```bash
$ mvn clean install
$ docker-compose up --build
$ curl http://localhost:8080/accounts -X POST -H "Content-Type: application/json" -H -d '{"customerID":"57f4dadc6d138cf005711f4e", "initialCredit":"2000.00"}'
$ curl http://localhost:8080/accounts
```

### Build
[![Build Status](https://secure.travis-ci.org/armandorvila/cloud-native-accounts.png)](http://travis-ci.org/armandorvila/cloud-native-accounts)  [![codecov.io](https://codecov.io/github/armandorvila/cloud-native-accounts/coverage.svg)](https://codecov.io/github/armandorvila/cloud-native-accounts) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/62c434b415f444e48bbed29f83b57a1f)](https://www.codacy.com/app/armandorvila/cloud-native-accounts?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=armandorvila/cloud-native-accounts&amp;utm_campaign=Badge_Grade)
