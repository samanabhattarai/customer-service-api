# Customer Management API

A Spring Boot REST API to manage customer records with tier-based classification.

## Build & Run
```
mvn clean install

mvn spring-boot:run
```
### Development with GitHub Codespaces

This project is configured for GitHub Codespaces with Java 21. To get started:

1. Click on the "Code" button in the GitHub repository
2. Select the "Codespaces" tab
3. Click "Create codespace on main"

The environment will be automatically set up with Java 21, Maven, and necessary VS Code extensions.


## Request and Response samples

### Sample request for creating a new customer
```
POST /customers
{
"name": "John Doe",
"email": "john@test.com",
"annualSpend": 500,
"lastPurchaseDate": "2025-06-11T18:26:25.317Z"
}
```

### Sample request to update a customer with an id
```
PUT /customers/f194d339-2511-4ede-bd87-3c5790625988
{
"name": "John Doe",
"email": "john@test.com",
"annualSpend": 500,
"lastPurchaseDate": "2025-06-11T18:26:25.317Z"
}
```

### Sample request to get all customers
```
GET /customers
```

### Sample request to get customers by name
```
GET /customers?name=John
``` 

### Sample request to get customers by email
```
GET /customers?email=jane@test.com
```

### Sample request to delete a customer
```
DELETE /customers/f194d339-2511-4ede-bd87-3c5790625988
```

### Response body for customer details
```
{
"id": "f194d339-2511-4ede-bd87-3c5790625988",
"name": "John Doe",
"email": "john@test.com",
"annualSpend": 500,
"tier": "Silver",
"lastPurchaseDate": "2025-06-11T18:26:25.317Z"
}
```

## Assumptions
- The API uses a simple in-memory database to store customer records.
- The API does not handle authentication or authorization.
- Database changes are managed using Liquibase.
- The API assumes the customer tier will be Silver if "last purchase date" is not available even if the spend is over 1000
- H2 console is available at http://localhost:8080/h2-console for local development (username: sa, password: empty)
