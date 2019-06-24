package dsop.konsument.kontrakt;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.pactfoundation.consumer.dsl.LambdaDslObject;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

public class ContractDefinition {

    private static final String PARTY_ID = "909716212";
    private static final String PARTY_ID_NO_ACCOUNTS = "123456789";
    private static final String LEGAL_MANDATE = "Skatteforvaltningsloven%20%C2%A7%2010-2%201";
    private static final String CORRELATION_ID_ACCOUNT_LIST = "14aea0c2-0742-4b84-8ac9-0844d05d4673";
    private static final String CORRELATION_ID_ACCOUNT_LIST_EMPTY = "fa9d1bcf-e6f5-47ec-95b8-37e47e2d0868";
    private static final String CORRELATION_ID_ACCOUNT_DETAILS = "5cba4c0d-afc3-4e96-b6c8-e9de2e81a31d";
    private static final String CORRELATION_ID_CARDS = "c049c99f-8a60-41ea-90c9-1889471394d0";
    private static final String CORRELATION_ID_ROLES = "8981b032-bdc2-4a01-a9d4-f0e5d938cce9";
    private static final String CORRELATION_ID_TRANSACTIONS = "b2e25cd6-8bb6-40c1-9aa8-29d7ca114cb3";

    private static final String AUTHORIZATION = "Bearer eyJraWQiOiJjWmbwME1rbTVIQzRnN3Z0"
        + "NmNwUDVGSFpMS0pzdzhmQkFJdUZiUzRSVEQ0IiwiYWxnIjoiUlMyNTYifQ.eyJh"
        + "dWQiOiJvaWRjX2JpdHNfc2thdHRlZXRhdGVuIiwic2NvcGUiOiJiaXRzOmt1bmRl"
        + "Zm9yaG9sZCIsImlzcyI6Iah0dHBzOlwvXC9vaWRjLXZlcjIuZGlmaS5ub1wvaWRw"
        + "b3J0ZW4tb2lkYy1wcm92aWRlclwvIiwidG9rZW5fdHlwZbI6IkJlYXJlciIsImV4"
        + "cCI6MTU1NTA2MjE2OSwiaWF0IjoxNTU1MDYyMDQ5LCJjbGllbnRfb3Jnbm8iOiI5"
        + "NzQ3NjEwNzYiLCJqdGkiOiJxS0l0bXpQakh4cEdxSC1rcEtXZ21Pc25DbkxWaU03"
        + "LU9hbnBEcDZxd2NJPSJ9.1qQ2jjequOz0OLKbvlCIa8oqzmsWQ1cYmsO5w4y4ufO"
        + "-_o5sQQ4VX6jqdc3Lrop2VYe9gyggDBsj1oMhzAcF0u09FmmEc752Mlv3ALWT_Yg"
        + "MLdcrz03jnfR9FE1GdhDtSQlJWkU-Oq9Izoxlc8lCKXOdJnNFjUFpm_pgfHe0LkC"
        + "DLCPV7AsrCGXxhsvXWHRLpTZ0a1rp7hQEMb9_uzlbyWC2ztOGQJGEZlysl1iI5HJ"
        + "v-Vzp2Y_DJ03NvtZN1ZYbsjBB-3__kGj6He6URuSj3bJp0FErRhbyAOVuZgxMlxT"
        + "AoeVdTqFDHeQMyF4vUNY_83a-2fkFa6RPdZX_2OlXmQ";

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("bank_provider", "localhost", 8080, this);

    @Pact(consumer = "etat_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws ParseException {
        DslPart pactAccountsBody = getAccountListDslPart();
        DslPart pactAccountDetailsBody = getAccountDetailsDslPart();
        DslPart pactCardsBody = getCardsDslPart();
        DslPart pactRolesBody = getRolesDslPart();
        DslPart pactTransactionsBody = getTransactionsDslPart();
        DslPart pactEmptyAccountsBody = getEmptyAccountsDslPart();

        Map<String, String> Listheaders = new HashMap<>();
        Listheaders.put("PartyID", PARTY_ID);
        Listheaders.put("Legal-Mandate", LEGAL_MANDATE);
        Listheaders.put("CorrelationID", CORRELATION_ID_ACCOUNT_LIST);
        Listheaders.put("Authorization", AUTHORIZATION);

        Map<String, String> EmptyListHeaders = new HashMap<>();
        EmptyListHeaders.put("PartyID", PARTY_ID_NO_ACCOUNTS);
        EmptyListHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        EmptyListHeaders.put("CorrelationID", CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        EmptyListHeaders.put("Authorization", AUTHORIZATION);

        Map<String, String> accountDetailsHeaders = new HashMap<>();
        accountDetailsHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        accountDetailsHeaders.put("CorrelationID", CORRELATION_ID_ACCOUNT_DETAILS);
        accountDetailsHeaders.put("Authorization", AUTHORIZATION);

        Map<String, String> cardsHeaders = new HashMap<>();
        cardsHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        cardsHeaders.put("CorrelationID", CORRELATION_ID_CARDS);
        cardsHeaders.put("Authorization", AUTHORIZATION);

        Map<String, String> rolesHeaders = new HashMap<>();
        rolesHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        rolesHeaders.put("CorrelationID", CORRELATION_ID_ROLES);
        rolesHeaders.put("Authorization", AUTHORIZATION);

        Map<String, String> transactionsHeaders = new HashMap<>();
        transactionsHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        transactionsHeaders.put("CorrelationID", CORRELATION_ID_TRANSACTIONS);
        transactionsHeaders.put("Authorization", AUTHORIZATION);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");

        return builder
        .given("test GET AccountList")
            .uponReceiving("GET AccountList REQUEST") // lag beskrivelse av testen
            .path("/accounts")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .headers(Listheaders)
            .method("GET")
            .willRespondWith()
            .headers(responseHeaders)
            .status(200)
            .body(pactAccountsBody)

        .given("test GET AccountDetails")
            .uponReceiving("GET AccountDetails REQUEST")
            .path("/accounts/5687123451")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(accountDetailsHeaders)
            .willRespondWith()
            .headers(responseHeaders)
            .status(200)
            .body(pactAccountDetailsBody)

        .given("test GET Cards")
            .uponReceiving("GET Cards REQUEST")
            .path("/accounts/5687123451/cards")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(cardsHeaders)
            .willRespondWith()
            .headers(responseHeaders)
            .status(200)
            .body(pactCardsBody)

        .given("test GET Roles")
            .uponReceiving("GET Roles REQUEST")
            .headers(rolesHeaders)
            .path("/accounts/5687123451/roles")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .willRespondWith()
            .headers(responseHeaders)
            .status(200)
            .body(pactRolesBody)

        .given("test GET Transactions")
            .uponReceiving("GET Transactions REQUEST")
            .path("/accounts/5687123451/transactions")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(transactionsHeaders)
            .willRespondWith()
            .headers(responseHeaders)
            .status(200)
            .body(pactTransactionsBody)

            // legg på negative tester
        .given("test GET empty AccountList")
            .uponReceiving("GET empty AccountList REQUEST")
            .path("/accounts")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(EmptyListHeaders)
            .willRespondWith()
            .headers(responseHeaders)
            .status(200)
            .body(pactEmptyAccountsBody)
            .toPact();
    }

    @Test
    @PactVerification()
    public void shouldVerifyProviderResponses() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders accountListHeaders = new HttpHeaders();
        HttpHeaders emptyAccountListHeaders = new HttpHeaders();
        HttpHeaders detailsHeaders = new HttpHeaders();
        HttpHeaders cardsHeaders = new HttpHeaders();
        HttpHeaders rolesHeaders = new HttpHeaders();
        HttpHeaders transactionHeaders = new HttpHeaders();

        createRequestHeaders(accountListHeaders, emptyAccountListHeaders, detailsHeaders, cardsHeaders, rolesHeaders, transactionHeaders);

        verifyAccountList(restTemplate, accountListHeaders);
        verifyEmptyAccountList(restTemplate, emptyAccountListHeaders);
        verifyAccountDetails(restTemplate, detailsHeaders);
        verifyTransactions(restTemplate, transactionHeaders);
        verifyCards(restTemplate, cardsHeaders);
        verifyRoles(restTemplate, rolesHeaders);

    }

    private void createRequestHeaders(HttpHeaders accountListHeaders, HttpHeaders emptyAccountListHeaders,
        HttpHeaders detailsHeaders, HttpHeaders cardsHeaders, HttpHeaders rolesHeaders, HttpHeaders transactionHeaders) {

        accountListHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        accountListHeaders.set("PartyID", PARTY_ID);
        accountListHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        accountListHeaders.set("CorrelationID", CORRELATION_ID_ACCOUNT_LIST);
        accountListHeaders.set("Authorization", AUTHORIZATION);

        emptyAccountListHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        emptyAccountListHeaders.set("PartyID", PARTY_ID_NO_ACCOUNTS);
        emptyAccountListHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        emptyAccountListHeaders.set("CorrelationID", CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        emptyAccountListHeaders.set("Authorization", AUTHORIZATION);

        detailsHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        detailsHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        detailsHeaders.set("CorrelationID", CORRELATION_ID_ACCOUNT_DETAILS);
        detailsHeaders.set("Authorization", AUTHORIZATION);

        cardsHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        cardsHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        cardsHeaders.set("CorrelationID", CORRELATION_ID_CARDS);
        cardsHeaders.set("Authorization", AUTHORIZATION);

        rolesHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        rolesHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        rolesHeaders.set("CorrelationID", CORRELATION_ID_ROLES);
        rolesHeaders.set("Authorization", AUTHORIZATION);

        transactionHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        transactionHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        transactionHeaders.set("CorrelationID", CORRELATION_ID_TRANSACTIONS);
        transactionHeaders.set("Authorization", AUTHORIZATION);
    }

    private void verifyRoles(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String RolesUrl = mockProvider.getUrl() + "/accounts/5687123451/roles?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> rolesResponse = sendRequest(restTemplate, accountCommonHeaders, RolesUrl);
        String jsonRoles = rolesResponse.getBody();

        System.out.println("Roles : " + jsonRoles);
        Roles roles = unmarhalRoles(jsonRoles);
        System.out.println(roles.getRoles());
    }

    private void verifyCards(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String cardsUrl = mockProvider.getUrl() + "/accounts/5687123451/cards?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> cardsResponse = sendRequest(restTemplate, accountCommonHeaders, cardsUrl);

        String jsonCards = cardsResponse.getBody();
        System.out.println("Cards : " + jsonCards);
        Cards cards = unmarhalCards(jsonCards);
        System.out.println(cards.getPaymentCards());
    }

    private void verifyTransactions(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String transactionsUrl =
            mockProvider.getUrl() + "/accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> transactionsResponse = sendRequest(restTemplate, accountCommonHeaders, transactionsUrl);

        String jsonTransactions = transactionsResponse.getBody();
        System.out.println("Transactions : " + jsonTransactions);
        Transactions transactions = unmarhalTransactions(jsonTransactions);
        System.out.println(transactions.getTransactions());
    }

    private void verifyAccountDetails(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountDetailsUrl = mockProvider.getUrl() + "/accounts/5687123451?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountDetailsResponse =
            sendRequest(restTemplate, accountListHeaders, accountDetailsUrl);

        String jsonAccountDetails = accountDetailsResponse.getBody();
        System.out.println("AccountDetails : " + jsonAccountDetails);
        AccountDetails accountDetails = unmarhalAccountDetails(jsonAccountDetails);
        System.out.println(accountDetails.getAccount());
    }

    private void verifyAccountList(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountList = mockProvider.getUrl() + "/accounts?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountsResponse =
            sendRequest(restTemplate, accountListHeaders, accountList);

        String jsonAccounts = accountsResponse.getBody();
        System.out.println("AccountList : " + jsonAccounts);
        Accounts accounts = unmarhalAccount(jsonAccounts);
        System.out.println(accounts.getAccounts());
    }

    private void verifyEmptyAccountList(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountList = mockProvider.getUrl() + "/accounts?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountsResponse =
            sendRequest(restTemplate, accountListHeaders, accountList);

        String jsonAccounts = accountsResponse.getBody();
        System.out.println(jsonAccounts);
        Accounts accounts = unmarhalAccount(jsonAccounts);
        System.out.println(accounts.getAccounts());
    }

    private ResponseEntity<String> sendRequest(RestTemplate restTemplate, HttpHeaders accountListHeaders,
                                               String transactionsUrl) {
        HttpEntity<String> entityTransactions = new HttpEntity<>(accountListHeaders);
        return restTemplate.exchange(transactionsUrl, HttpMethod.GET, entityTransactions, String.class);
    }

    private DslPart getAccountDetailsDslPart() throws ParseException {

        Date registered = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-05-20T10:23:38");
        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date endDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((accountDetails) -> {
            accountDetails.stringValue("responseStatus", "complete");
            accountDetails.object("account", account -> {
                account.stringValue("status", "enabled"); // enum
                addServicer(account); // se over
                account.stringValue("accountIdentifier", "78770517388"); // se over
                account.stringValue("accountReference", "MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr"); // se over
                account.stringValue("type", "loanAccount"); //enum
                account.stringValue("currency", "NOK");  //se over
                account.array("balances", balance ->
                    balance.object(balanceObject -> {
                        balanceObject.booleanType("creditLineIncluded", true);
                        balanceObject.numberValue("amount", 20.2); // type så lenge det er med 2 desimaler
                        balanceObject.stringValue("creditDebitIndicator", "credit"); // enum
                        balanceObject.date("registered", "yyyy-MM-dd'T'HH:mm:ss", registered);
                        balanceObject.stringValue("type", "availableBalance"); //enum
                        balanceObject.numberValue("creditLineAmount", 10.9); //se over
                        balanceObject.stringValue("creditLineCurrency", "NOK"); //se over
                    }));
                addPrimaryOwner(startDate, endDate, account);
            });
        }).build();
    }

    private DslPart getAccountListDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((accountsList) -> {
            accountsList.stringValue("responseStatus", "complete");
            accountsList.array("accounts", accounts -> accounts.object(accountObject -> {
                accountObject.stringValue("status", "enabled");
                addServicer(accountObject);

                accountObject.array("links", links ->
                    links
                        .object(link -> {
                            link.stringType("rel", "cards");
                            link.stringType("href",
                                "/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/" + "cards");
                        })
                        .object(link -> {
                            link.stringType("rel", "roles");
                            link.stringType("href",
                                "/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/" + "roles");
                        })
                        .object(link -> {
                            link.stringType("rel", "transactions");
                            link.stringType("href",
                                "/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/" + "transactions");
                        })
                );
                accountObject.stringValue("accountIdentifier", "78770517388"); // 11 siffer regexp
                accountObject.stringValue("accountReference", "MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr"); //.* string -> StringType
                accountObject.stringValue("type", "loanAccount"); //stringMatcher test mot alle verdier
                accountObject.stringValue("currency", "NOK"); // Stringmatcher Uppercase 3 letters A-Z
                addPrimaryOwner(startDate, expiryDate, accountObject);
                addElectronicAdress(accountObject, "electronicAddresses", "type", "96711125");
                accountObject.stringValue("name", "Boomsma Erika");
            }));
        }).build();
    }

    private DslPart getEmptyAccountsDslPart() throws ParseException {
        return newJsonBody((accountsList) -> {
            accountsList.stringValue("responseStatus", "complete");
            accountsList.array("accounts", account -> {
            });
        }).build();
    }

    private DslPart getCardsDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm").parse("2010-05");
        Date expiryDate = new SimpleDateFormat("yyyy-mm").parse("2010-05");

        return newJsonBody((cardsBody) -> {
            cardsBody.stringValue("responseStatus", "complete");
            cardsBody.array("paymentCards", paymentCards -> paymentCards.object(cardObject -> {
                addCardIdentifier(startDate, expiryDate, cardObject);
            }));
        }).build();
    }

    private DslPart getRolesDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date endDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((rolesBody) -> {
            rolesBody.stringValue("responseStatus", "complete"); //enum
            rolesBody.array("roles", roles -> roles.object(roleObject -> {
                roleObject.stringValue("name", "Nicolai"); //string
                roleObject.date("startDate", "yyyy-mm-dd", startDate); // sjekk oblig.
                roleObject.date("endDate", "yyyy-mm-dd", endDate); // sjekk oblig.
                roleObject.object("postalAdress", postalAdress -> {
                    postalAdress.stringValue("postCode", "1598");
                    postalAdress.stringValue("type", "residential");
                    postalAdress.stringValue("streetName", "trysilgata");
                    postalAdress.stringValue("buildingNumber", "2");
                    postalAdress.stringValue("townName", "Oslo");
                    postalAdress.stringValue("country", "Norway");
                });
                addIdentifier(roleObject);
                roleObject.array("electronicAddresses", electronicAddress ->
                    electronicAddress.object(electronicAddressObject -> {
                        electronicAddressObject.stringValue("type", "emailAddress");
                        electronicAddressObject.stringValue("value", "test@test.no");
                    }));
                roleObject.stringValue("permission", "rightToUseAlone");
            }));
        }).build();
    }

    private DslPart getTransactionsDslPart() throws ParseException {
        Date bookingDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-05-20T10:23:38");
        Date valueDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-04-20T10:23:38");
        Date registredDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-04-20T10:23:38");
        Date startDate = new SimpleDateFormat("yyyy-mm").parse("2010-05");
        Date expiryDate = new SimpleDateFormat("yyyy-mm").parse("2010-05");

        return newJsonBody((transactionsBody) ->
            transactionsBody.array("transactions", transactions -> transactions.object(transactionsObject -> {
                transactionsObject.date("bookingDate", "yyyy-MM-dd'T'HH:mm:ss", bookingDate);
                transactionsObject.date("valueDate", "yyyy-MM-dd'T'HH:mm:ss", valueDate);
                transactionsObject.stringValue("family", "additionalMiscellaneousCreditOperations");
                transactionsObject.stringValue("transactionIdentifier", "DSOP10000000318308");
                transactionsObject.booleanType("reversalIndicator", true);
                transactionsObject.stringValue("status", "booked");
                transactionsObject.date("registered", "yyyy-MM-dd'T'HH:mm:ss", registredDate);
                transactionsObject.numberValue("amount", 100.34);
                transactionsObject.stringValue("currency", "NOK");
                transactionsObject.stringValue("additionalInfo", "info");
                transactionsObject.stringValue("merchant", "Power");
                transactionsObject
                    .object("paymentCard", paymentCard -> addCardIdentifier(startDate, expiryDate, paymentCard));
                addIdentifier(transactionsObject);
                transactionsObject.object("transactionCode", transactionCode -> {
                    transactionCode.stringValue("domain", "accountManagement");
                    transactionCode.stringValue("family", "additionalMiscellaneousCreditOperations");
                    transactionCode.stringValue("subFamily", "valueDate"); // sjekke
                    transactionCode.stringValue("freeText", "VISA Varekjøp");
                });
                transactionsObject.array("counterParties", counterParty ->
                    counterParty.object(counterPartyObject -> {
                        addIdentifier(counterPartyObject);
                        counterPartyObject.stringValue("accountIdentifier", "9867123111");
                        counterPartyObject.stringValue("name", "Selskapet AS");
                        counterPartyObject.stringValue("type", "creditor");
                        addPostalAdress(counterPartyObject);
                    }));
            }))).build();
    }

    private void addCardIdentifier(Date startDate, Date expiryDate, LambdaDslObject parentDslObject) {
        parentDslObject.stringValue("holderName", "Alma"); // String
        parentDslObject.stringValue("cardIssuerName", "Mastercard AS"); //String
        parentDslObject.stringValue("type", "creditCard"); //Enum
        parentDslObject.date("startDate", "yyyy-mm", startDate); // må være med
        parentDslObject.date("expiryDate", "yyyy-mm", expiryDate); // må være med
        parentDslObject.stringValue("cardIdentifier", "4567xxxxxxxx9809"); // maskert regmatcher X eller *
        parentDslObject.object("cardIssuerIdentifier", cardIssuerIdentifier -> {
            cardIssuerIdentifier.stringValue("countryOfResidence", "NO"); // se over
            cardIssuerIdentifier.stringValue("value", "123456879"); // se over
            cardIssuerIdentifier.stringValue("type", "nationalIdentityNumber"); // se over
        });
    }

    private void addPostalAdress(LambdaDslObject parentDslObject) {
        parentDslObject.object("postalAddress", postalAddress -> {
            postalAddress.stringValue("postCode", "1598");
            postalAddress.stringValue("type", "residential");
            postalAddress.stringValue("streetName", "trysilgata");
            postalAddress.stringValue("buildingNumber", "2");
            postalAddress.stringValue("townName", "Oslo");
            postalAddress.stringValue("country", "Norway");
            postalAddress.array("addressLines", addressLine -> addressLine.stringValue("bondes vei 4"));
        });
    }

    private void addPrimaryOwner(Date startDate, Date expiryDate, LambdaDslObject parentDslObject) {
        parentDslObject.object("primaryOwner", primaryOwner -> {
            primaryOwner.stringValue("permission", "noRight"); //StringMatch enums
            addIdentifier(primaryOwner);
            primaryOwner.stringValue("name", "Boomsma Erika"); // String
            primaryOwner.date("startDate", "yyyy-mm-dd", startDate);
            primaryOwner.date("endDate", "yyyy-mm-dd", expiryDate);
            addPostalAdress(primaryOwner);
        });
    }

    private void addServicer(LambdaDslObject account) {
        account.object("servicer", servicer -> {
            servicer.stringValue("name", "DNB");
            servicer.object("identifier", identifier -> {
                identifier.stringValue("countryOfResidence", "NO");
                identifier.stringValue("value", "123456879");
                identifier.stringValue("type", "countryIdentificationCode");
            });
        });
    }

    private void addIdentifier(LambdaDslObject parentDslObject) {
        parentDslObject.object("identifier", identifier -> {
            identifier.stringValue("countryOfResidence", "NO"); //ISO standard 2 bokstaver
            identifier.stringValue("value", "10108054242");
            identifier.stringValue("type", "nationalIdentityNumber"); // en
        });

    }

    private void addLinks(LambdaDslObject accountObject, String links2, String rel,
                                     String type, String href) {
        accountObject.array(links2, links ->
            links.object(link -> {
                link.stringType(rel, type);
                link.stringType(href, "/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/" + type);
            }));
    }

    private void addElectronicAdress(LambdaDslObject accountObject, String arrayName, String type, String value) {
        accountObject.array(arrayName, links ->
            links.object(electronicAddressObject -> {
                electronicAddressObject.stringType("type", type);
                electronicAddressObject.stringType("value", value);
            }));
    }


    private Accounts unmarhalAccount(String kontolisteJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Accounts accountRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            accountRoot = objectMapper.readValue(kontolisteJson, Accounts.class);
            return accountRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Kontoliste kan ikke unmarshalles", e);
        }
    }

    private AccountDetails unmarhalAccountDetails(String kontodetaljerJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AccountDetails accountDetailsRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            accountDetailsRoot = objectMapper.readValue(kontodetaljerJson, AccountDetails.class);
            return accountDetailsRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Kontodetaljer kan ikke unmarshalles", e);
        }
    }

    private Cards unmarhalCards(String cardsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Cards cardsRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            cardsRoot = objectMapper.readValue(cardsJson, Cards.class);
            return cardsRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Cards kan ikke unmarshalles", e);
        }
    }

    private Roles unmarhalRoles(String rolesJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Roles rolesRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            rolesRoot = objectMapper.readValue(rolesJson, Roles.class);
            return rolesRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Cards kan ikke unmarshalles", e);
        }
    }

    private Transactions unmarhalTransactions(String transactionsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Transactions transactionsRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            transactionsRoot = objectMapper.readValue(transactionsJson, Transactions.class);
            return transactionsRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Transactions kan ikke unmarshalles", e);
        }
    }
}
