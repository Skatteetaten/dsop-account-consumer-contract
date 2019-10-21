
# dsop-account-consumer-contract
[Consumer driven contract tests](https://martinfowler.com/articles/consumerDrivenContracts.html) 
involves Providers(financial institutions) and Consumers(agencies).
A contract specifies the interaction between providers and consumers. 
Tests are implemented based on the shared contract.  

This project contains a REST contract first pattern and a Consumer Driven Contract pattern. 
The former is implemented with OPEN API, the latter with PACT. 
They complement eachother and give api congruity across services.
An implementation of the OPEN API specification can be found in the AccountsApiImpl class.
The PACT contract can be found in resources/mypacts. 

This project provides a pact test example. 
The financial institutions are meant to create their own tests.

## Scope
Pact contract tests meet the following requirements:
1. Tests that all fields have the correct name
2. Tests that values have the correct format
3. Tests that the headers in the request and the response have the correct name and format
4. Tests several states.

This ensures that the response from the providers is congruent with the expectations from the consumer 

## Audience
This document is intented for developers and testers.

## Test cases
### 200 scenario
| Test state               | Interaction                                           |
| -------------            | -------------                                         |
| test GET AccountList     | Sends an AccountList request with PartyID: 909716212  |
| test GET AccountDetails  | Sends an AccountDetails request for the given party   |
| test GET Roles           | Sends a Roles request for the given party             |
| test GET Cards           | Sends a Cards request for the given party             |
| test GET Transactions    | Sends a Transaction request for the given party       |

### Other test scenarios
| Test state                           | Interaction                                                                                                  |
| -------------                        | -------------                                                                                                |
| test GET empty AccountList           | Sends an AccountList request with PartyID: 123456789. Expects Empty Accountlist                              |
| test GET worng header AccountList    | Sends an AccountList request with wrong Legal-Mandate header and PartyID: 124678913. Expects 400 bad request |


## Getting started
1. Download the [Pact Contract](src/main/resources/mypacts/etat_consumer-bank_provider.json)
to your project. 
2. Configure your maven dependency to import pact
```
    <dependency>
      <groupId>au.com.dius</groupId>
      <artifactId>pact-jvm-provider-spring_2.12</artifactId>
      <version>${your.version}</version>
    </dependency>
    <dependency>
      <groupId>au.com.dius</groupId>
      <artifactId>pact-jvm-matchers_2.12</artifactId>
      <version>${your.version}</version>
    </dependency>
```
2. Write a test class. (Here is one [example](src/test/java/dsop/konsument/kontrakt/ProviderIntegrationTest.java). Implement your own)
3. Configure your test class to run the tests. See the implmentation [section](#Implemenation) for more details. We have 6 tests you need to write. You will find an example implementation in the class mentionted above..
3. Run the tests as a part of your test stage. 


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
  - First page

* @State("test GET empty AccountList")
  - Account Empty List: /accounts?fromDate=2016-12-09&toDate=2016-12-09 (PartyID: 123456789)

* @State("test GET wrong header AccountList")
  - Account Empty List wrongHeader: /accounts?fromDate=2016-12-09&toDate=2016-12-09

For more information about the requests see the json pact file. The **interactions** field 
contains an array of requests that will be executed during test. 

The **response.body** field contains the response that is excpected from the financian instiutuions.

The **request** field contains the information about the request that will be sent by PACT

### Implemenation
The Requests will be sent when PACT's TestTarget and a test class is implemented.
The financial institutions will have to mock or create test data. The test data will 
create a response that matches the PACT response for a given state. 

Implementation Provider (financial Institution)
1. Inserts pact file in a project folder
1. Inserts PACT dependency
1. Creates test class for pact provider tests
1. Wires up test class with PACT. You will see the following annotations for the test class in our example.
Implement your own.  
```
@RunWith(SpringRestPactRunner.class)
@Provider("bank_provider")
@PactFolder("mypacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8080")
public class ProviderIntegrationTest {

    @TestTarget
    public final Target target = new SpringBootHttpTarget();

    @State("test GET AccountList")
    public void getAccountList() {
        String responseList = getResposeFromFile("responses/AccountList.json");
        Accounts accounts = unmarhalAccount(responseList);
        doReturn(accounts).when(accountsService).getAccounts("909716212");
    }

```


   1. @Provider("bank_provider")
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
    private AccountsService accountsService;


    @State("test GET AccountList")
    public void getAccountList() {
        String responseList = getResposeFromFile("responses/AccountList.json");
        Accounts accounts = unmarhalAccount(responseList);
        doReturn(accounts).when(accountsService).getAccounts("909716212");
    }
```


