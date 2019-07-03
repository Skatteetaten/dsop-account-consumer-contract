# dsop-account-consumer-contract

This project contains a REST contract first pattern and a Consumer Driven Contract pattern. 
The former is implemented with OPEN API, the latter with PACT. 
They complement eachother and give api congruity across services.
An implementation of the OPEN API specification can be found in the AccountsApiImpl class.
The PACT contract can be found in resources/mypacts

## Scope

Pact contract and tests meets the following requirements:
1. Tests that all fields have the correct name
2. Tests that values have the correct format
3. Tests that the headers in the request and the response have the correct name and format
4. Tests several states.

This ensures that the response from the providers is congruent with the expectations from the consumer 

## Providers
 Consumer Driven Contracts is a pattern that solves the challange of having many consumers and providers.
Pact is used as a framework for the implementation of Consumer Driven Contracts.
The providers(financial institutions) implement a pact-file and configure tests. 
Below is a description of the REST requests that will be made by pact.
 
The tests assume a **JSON response**. In production the response must be encrypted.

### Requests
The following states should be tested by the providers:
 
* @State("test GET AccountList")
  - Account List: /accounts?fromDate=2016-12-09&toDate=2016-12-09 (PartyID: 909716212)

* @State("test GET AccountDetails")
  - Account Details: /accounts/5687123451?fromDate=2016-12-09&toDate=2016-12-09

* @State("test GET Roles")
  - Account Roles : /accounts/5687123451/roles?fromDate=2016-12-09&toDate=2016-12-09

* @State("test GET Cards")
  - Account Cards : /accounts/5687123451/cards?fromDate=2016-12-09&toDate=2016-12-09

* @State("test GET Transactions")
  - Account Transactions : /accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09

* @State("test GET empty AccountList")
  - Account Empty List: /accounts?fromDate=2016-12-09&toDate=2016-12-09 (PartyID: 123456789)

For more information about the requests see the json pact file. The **interactions** field 
contains an array of requests that will be executed during test. 

The **response.body** field contains the response that is excpected from the financian instiutuions.

The **request** field contains the information about the request that will be sent by PACT

### Implemenation
The Requests will be sent when PACT's TestTarget and a test class is implemented.
The financial institutions will have to mock or create test data. The test data will 
create a response that matches the PACT response for a given state. 

Implementation Provider (financial Institution)
1. Inserts file in a project folder
1. Inserts PACT dependency
1. Creates test class for pact provider tests
1. Wires up test class with PACT
   1. @Provider([Provider name])
      1. The provider name is stated in the pact file
   1. @PactFolder([Folder name])
      1. The folder where the pact file is located.
   1. @TestTarget
      1. Specifies the the protocol, host and host of your backend
   1. @State([testState])
      1. The state that is under test. The relevant states are defined in the pact (providerStates.name)               
   1.	Mock and stub the required classes to produce the expected response
   
   Example of mocking a bean to produce the exptected response : 
```
    @MockBean
    private Service service;


    @State({"test State"})
    public void toState() {
        when(service.get(anyString()).thenReturn("requiredValue");
    }
```

 