# dsop-account-consumer-contract

This project contains a REST contract first pattern and an Consumer Driven Contract pattern. 
The former is implemented with OPEN API, the latter with PACT.
They complement eachother and combined implement a Congruity API pattern across services.

## Scope
Pact kontrakt/tester skal dekke følgende behov:
1. Tester at alle felter har riktig navn
2. Tester at verdier har riktig format
3. Tester at alle parametere i requesten har riktig navn og format
4. Tester at alle headere i requesten og responsen har riktig navn og format
5. Tester "state" - forskjellige http status koder og riktig svar i samsvar med forventet format og verdier
6. Tester at vi kan bygge DTO objekter med json svar som bankene returnerer

## Providers
 Consumer Driven Contracts is a pattern that solves the challange of having many consumers and providers.
Pact is used as a framework for the implementation of Consumer Driven Contracts.
The providers(financial institutions) implement a pact-file and configure tests. 
Below is a description of the REST requests that will be made by pact.
 
The tests assume a **JSON response**. In production the response must be encrypted.

### Requests
* Account List: /accounts?fromDate=2016-12-09&toDate=2016-12-09 (PartyID: 909716212)

* Account Details: /accounts/5687123451?fromDate=2016-12-09&toDate=2016-12-09

* Account Roles : /accounts/5687123451/roles?fromDate=2016-12-09&toDate=2016-12-09

* Account Cards : /accounts/5687123451/cards?fromDate=2016-12-09&toDate=2016-12-09

* Account Transactions : /accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09

* Account Empty List: /accounts?fromDate=2016-12-09&toDate=2016-12-09 (PartyID: 123456789)

For more information about the requests see the json pact file. The **interactions** field 
contains an array of requests that will be executed during test. 

The **response.body** field contains the response that is excpected from the financian instiutuions.

The **request** field contains the information about the request that will be sent by PACT

### Implemenation
The Requests will be sent when PACT's TestTarget and a test class is implemented.
The financial institutions will have to mock or create test data. The test data will 
create a response that matches the PACT response for a given state. 

 