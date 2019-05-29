package dsop.konsument.kontrakt;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private static final String CORRELATION_ID = "77dcfc8c-5f6d-4095-bb7c-cdb54b5067a5";
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
        + "AoeVdTqFDHeQMyF4vUNY_83a-2fkFa6RPdZX_2OlXmQ, CorrelationID=bbce7"
        + "f02-ae53-c44f-5c5b-aa04da2fbcdb";


    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("bank_provider", "localhost", 8080, this);

    @Pact(consumer = "etat_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws ParseException {
        DslPart pactAccountsBody = getAccountsDslPart();
        DslPart pactAccountDetailsBody = getAccountDetailsDslPart();
        DslPart pactCardsBody = getCardsDslPart();
        DslPart pactRolesBody = getRolesDslPart();
        DslPart pactTransactionsBody = getTransactionsDslPart();
        DslPart pactEmptyAccountsBody = getEmptyAccountsDslPart();

        Map<String, String> Listheaders = new HashMap<>();
        Listheaders.put("PartyID", PARTY_ID);
        Listheaders.put("Legal-Mandate", LEGAL_MANDATE);
        Listheaders.put("CorrelationID", CORRELATION_ID);
        Listheaders.put("Authorization", AUTHORIZATION);

        Map<String, String> EmptyListHeaders = new HashMap<>();
        EmptyListHeaders.put("PartyID", PARTY_ID_NO_ACCOUNTS);
        EmptyListHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        EmptyListHeaders.put("CorrelationID", CORRELATION_ID);
        EmptyListHeaders.put("Authorization", AUTHORIZATION);

        Map<String, String> CommonHeaders = new HashMap<>();
        CommonHeaders.put("Legal-Mandate", LEGAL_MANDATE);
        CommonHeaders.put("CorrelationID", CORRELATION_ID);
        CommonHeaders.put("Authorization", AUTHORIZATION);

        return builder
        .given("test GET AccountList")
            .uponReceiving("GET AccountList REQUEST") // lag beskrivelse av testen
            .path("/accounts")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .headers(Listheaders)
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(pactAccountsBody)

        .given("test GET AccountDetails")
            .uponReceiving("GET AccountDetails REQUEST")
            .path("/accounts/5687123451")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(CommonHeaders)
        .willRespondWith()
            .status(200)
            .body(pactAccountDetailsBody)

        .given("test GET Cards")
            .uponReceiving("GET Cards REQUEST")
            .path("/accounts/5687123451/cards")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(CommonHeaders)
        .willRespondWith()
            .status(200)
            .body(pactCardsBody)

        .given("test GET Roles")
            .uponReceiving("GET Roles REQUEST")
            .headers(CommonHeaders)
            .path("/accounts/5687123451/roles")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(pactRolesBody)

        .given("test GET Transactions")
            .uponReceiving("GET Transactions REQUEST")
            .path("/accounts/5687123451/transactions")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(CommonHeaders)
        .willRespondWith()
            .status(200)
            .body(pactTransactionsBody)

        // legg på negative tester
        .given("test GET AccountList empty")
            .uponReceiving("GET AccountList empty REQUEST")
            .path("/accounts")
            .query("fromDate=2016-12-09&toDate=2016-12-09")
            .method("GET")
            .headers(EmptyListHeaders)
        .willRespondWith()
            .status(200)
            .body(pactEmptyAccountsBody)
        .toPact();
    }

    @Test
    @PactVerification()
    public void shouldVerifyProviderResponses() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders accountListHeaders = new HttpHeaders();
        HttpHeaders accountCommonHeaders = new HttpHeaders();
        HttpHeaders emptyAccountListHeaders = new HttpHeaders();

        createRequestHeaders(accountListHeaders, accountCommonHeaders, emptyAccountListHeaders);

        verifyAccountList(restTemplate, accountListHeaders);
        verifyEmptyAccountList(restTemplate, emptyAccountListHeaders);
        verifyAccountDetails(restTemplate, accountListHeaders);
        verifyTransactions(restTemplate, accountCommonHeaders);
        verifyCards(restTemplate, accountCommonHeaders);
        verifyRoles(restTemplate, accountCommonHeaders);

    }

    private void createRequestHeaders(HttpHeaders accountListHeaders, HttpHeaders accountCommonHeaders, HttpHeaders emptyAccountListHeaders) {
        accountListHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        accountListHeaders.set("PartyID", PARTY_ID);
        accountListHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        accountListHeaders.set("CorrelationID", CORRELATION_ID);
        accountListHeaders.set("Authorization", AUTHORIZATION);

        emptyAccountListHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        emptyAccountListHeaders.set("PartyID", PARTY_ID_NO_ACCOUNTS);
        emptyAccountListHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        emptyAccountListHeaders.set("CorrelationID", CORRELATION_ID);
        emptyAccountListHeaders.set("Authorization", AUTHORIZATION);

        accountCommonHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        accountCommonHeaders.set("Legal-Mandate", LEGAL_MANDATE);
        accountCommonHeaders.set("CorrelationID", CORRELATION_ID);
        accountCommonHeaders.set("Authorization", AUTHORIZATION);
    }

    private void verifyRoles(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String RolesUrl = mockProvider.getUrl() + "/accounts/5687123451/roles?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> rolesResponse = sendRequest(restTemplate, accountCommonHeaders, RolesUrl);
        String jsonRoles = rolesResponse.getBody();

        System.out.println(jsonRoles);
        Roles roles = unmarhalRoles(jsonRoles);
        System.out.println(roles.getRoles());
    }

    private void verifyCards(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String cardsUrl = mockProvider.getUrl() + "/accounts/5687123451/cards?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> cardsResponse = sendRequest(restTemplate, accountCommonHeaders, cardsUrl);

        String jsonCards = cardsResponse.getBody();
        System.out.println(jsonCards);
        Cards cards = unmarhalCards(jsonCards);
        System.out.println(cards.getPaymentCards());
    }

    private void verifyTransactions(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String transactionsUrl = mockProvider.getUrl() + "/accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> transactionsResponse = sendRequest(restTemplate, accountCommonHeaders, transactionsUrl);

        String jsonTransactions = transactionsResponse.getBody();
        System.out.println(jsonTransactions);
        Transactions transactions = unmarhalTransactions(jsonTransactions);
        System.out.println(transactions.getTransactions());
    }

    private void verifyAccountDetails(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountDetailsUrl = mockProvider.getUrl() + "/accounts/5687123451?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountDetailsResponse =
            sendRequest(restTemplate, accountListHeaders, accountDetailsUrl);

        String jsonAccountDetails = accountDetailsResponse.getBody();
        System.out.println(jsonAccountDetails);
        AccountDetails accountDetails = unmarhalAccountDetails(jsonAccountDetails);
        System.out.println(accountDetails.getAccount());
    }

    private void verifyAccountList(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountList = mockProvider.getUrl() + "/accounts?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountsResponse =
            sendRequest(restTemplate, accountListHeaders, accountList);

        String jsonAccounts = accountsResponse.getBody();
        System.out.println(jsonAccounts);
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
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((accountDetails) -> {
            accountDetails.stringValue("responseStatus", "complete"); //enum
            accountDetails.object("account", account -> {
                account.stringValue("status", "enabled"); // enum
                createServicer(account); // se over
                createElectronicAdress(account, "links", "rel", "string", "href"); //se over
                account.stringValue("accountIdentifier", "string"); // se over
                account.stringValue("accountReference", "string"); // se over
                account.stringValue("type", "loanAccount"); //enum
                account.stringValue("currency", "string");  //se over

                account.array("balance", balance ->
                    balance.object(balanceObject -> {
                        balanceObject.booleanType("creditLineIncluded", true);
                        balanceObject.numberValue("amount", 0.2); // type så lenge det er med 2 desimaler
                        balanceObject.stringValue("creditDebitIndicator", "credit"); // enum
                        balanceObject.date("registered", "yyyy-MM-dd'T'HH:mm:ss", registered);
                        balanceObject.stringValue("type", "availableBalance"); //enum
                        balanceObject.numberValue("creditLineAmount", 10.9); //se over
                        balanceObject.stringValue("creditLineCurrency", "NOK"); //se over
                    }));

                account.object("primaryOwner", primaryOwner -> { // se over
                    primaryOwner.stringValue("name", "string");
                    primaryOwner.date("startDate", "yyyy-mm-dd", startDate);
                    primaryOwner.date("expiryDate", "yyyy-mm-dd", expiryDate);
                    createPostalAdress(primaryOwner);
                    createElectronicAdress(account, "electronicAddress", "type", "phoneNumber", "value");
                });
            });
        }).build();
    }

    private DslPart getAccountsDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");


        return newJsonBody((accountsList) -> {
            accountsList.stringValue("responseStatus", "complete");
            accountsList.array("accounts", accounts -> accounts.object(accountObject -> {
                accountObject.stringValue("status", "enabled");
                createServicer(accountObject);
                createElectronicAdress(accountObject, "links", "rel", "string", "href");

                accountObject.stringValue("accountIdentifier", "string"); // 11 siffer regexp
                accountObject.stringValue("accountReference", "string"); //.* string -> StringType
                accountObject.stringValue("type", "loanAccount"); //stringMatcher test mot alle verdier
                accountObject.stringValue("currency", "string"); // Stringmatcher Uppercase 3 letters A-Z
                accountObject.object("primaryOwner", primaryOwner -> {
                    primaryOwner.stringValue("permission", "noRight"); //StringMatch enums
                    primaryOwner.object("identifier", identifier -> {
                        identifier.stringValue("countryOfResidence", "string"); //ISO standard 2 bokstaver
                        identifier.stringValue("value", "string");
                        identifier.stringValue("type", "countryIdentificationCode"); // enum
                    });
                    primaryOwner.stringValue("name", "string"); // String
                    primaryOwner.date("startDate", "yyyy-mm-dd", startDate);
                    primaryOwner.date("expiryDate", "yyyy-mm-dd", expiryDate);
                    createPostalAdress(primaryOwner);
                });

                createElectronicAdress(accountObject, "electronicAddress", "type", "phoneNumber", "value"); // reformat
                accountObject.stringValue("name", "string");
            }));
        }).build();
    }

    private DslPart getEmptyAccountsDslPart() throws ParseException {
        return newJsonBody((accountsList) -> {
            accountsList.stringValue("responseStatus", "complete");
            accountsList.array("accounts", account -> {});
        }).build();
    }

    private DslPart getCardsDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((cardsBody) -> {
            cardsBody.stringValue("responseStatus", "complete");
            cardsBody.array("paymentCards", paymentCards -> paymentCards.object(cardObject -> {
                createCardIdentifier(startDate, expiryDate, cardObject);
            }));
        }).build();
    }

    private DslPart getRolesDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date endDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((rolesBody) -> {
            rolesBody.stringValue("responseStatus", "complete"); //enum
            rolesBody.array("roles", roles -> roles.object(roleObject -> {
                roleObject.stringValue("name", "string"); //string
                roleObject.date("startDate", "yyyy-mm-dd", startDate); // sjekk oblig.
                roleObject.date("endDate", "yyyy-mm-dd", endDate); // sjekk oblig.
                roleObject.object("postalAdress", postalAdress -> {
                    postalAdress.stringValue("postCode", "");
                    postalAdress.stringValue("type", "residential");
                    postalAdress.stringValue("streetName", "");
                    postalAdress.stringValue("buildingNumber", "");
                    postalAdress.stringValue("townName", "");
                    postalAdress.stringValue("country", "");
                });
                roleObject.object("identifier", identifier -> {
                    identifier.stringValue("countryOfResidence", "Norway");
                    identifier.stringValue("value", "countryIdentificationCode");
                    identifier.stringValue("type", "countryIdentificationCode");
                });
                roleObject.array("electronicAddress", electronicAddress ->
                    electronicAddress.object(electronicAddressObject -> {
                        electronicAddressObject.stringValue("type", "emailAddress");
                        electronicAddressObject.stringValue("value", "");
                    }));
                roleObject.stringValue("permission", "noRight");
            }));
        }).build();
    }

    private DslPart getTransactionsDslPart() throws ParseException {
        Date bookingDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-05-20T10:23:38");
        Date valueDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-04-20T10:23:38");
        Date registredDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-04-20T10:23:38");
        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((transactionsBody) ->
            transactionsBody.array("transactions", transactions -> transactions.object(transactionsObject -> {
                transactionsObject.date("bookingDate", "yyyy-MM-dd'T'HH:mm:ss", bookingDate);
                transactionsObject.date("valueDate", "yyyy-MM-dd'T'HH:mm:ss", valueDate);
                transactionsObject.stringValue("family", "additionalMiscellaneousCreditOperations");
                transactionsObject.stringValue("transactionIdentifier", "string");
                transactionsObject.booleanType("reversalIndicator", true);
                transactionsObject.stringValue("status", "booked");
                transactionsObject.date("registered", "yyyy-MM-dd'T'HH:mm:ss", registredDate);
                transactionsObject.numberValue("amount", 100.34);
                transactionsObject.stringValue("currency", "NOK");
                transactionsObject.stringValue("additionalInfo", "string");
                transactionsObject.stringValue("merchant", "string");
                transactionsObject.object("paymentCard", paymentCard -> createCardIdentifier(startDate, expiryDate, paymentCard));
                transactionsObject.object("identifier", identifier -> {
                    identifier.stringValue("countryOfResidence", "Norway");
                    identifier.stringValue("value", "countryIdentificationCode");
                    identifier.stringValue("type", "countryIdentificationCode");
                });
                transactionsObject.object("transactionCode", transactionCode -> {
                    transactionCode.stringValue("domain", "accountManagement");
                    transactionCode.stringValue("family", "additionalMiscellaneousCreditOperations");
                    transactionCode.stringValue("subFamily", "valueDate");
                    transactionCode.stringValue("freeText", "string");
                });
                transactionsObject.array("counterParty", counterParty ->
                    counterParty.object(counterPartyObject -> {
                        counterPartyObject.object("identifier", identifier -> {
                            identifier.stringValue("countryOfResidence", "Norway");
                            identifier.stringValue("value", "countryIdentificationCode");
                            identifier.stringValue("type", "countryIdentificationCode");
                        });
                        counterPartyObject.stringValue("accountIdentifier", "string");
                        counterPartyObject.stringValue("name", "string");
                        counterPartyObject.stringValue("type", "creditor");
                        createPostalAdress(counterPartyObject);
                    }));
            }))).build();
    }

    private void createCardIdentifier(Date startDate, Date expiryDate, LambdaDslObject paymentCard) {
        paymentCard.stringValue("holderName", "string"); // String
        paymentCard.stringValue("cardIssuerName", "string"); //String
        paymentCard.stringValue("type", "creditCard"); //Enum
        paymentCard.date("startDate", "yyyy-mm-dd", startDate); // må være med
        paymentCard.date("expiryDate", "yyyy-mm-dd", expiryDate); // må være med
        paymentCard.stringValue("cardIdentifier", "string"); // maskert regmatcher X eller *
        paymentCard.object("cardIssuerIdentifier", cardIssuerIdentifier -> {
            cardIssuerIdentifier.stringValue("countryOfResidence", "string"); // se over
            cardIssuerIdentifier.stringValue("value", "string"); // se over
            cardIssuerIdentifier.stringValue("type", "countryIdentificationCode"); // se over
        });
    }

    private void createPostalAdress(LambdaDslObject primaryOwner) {
        primaryOwner.object("postalAddress", postalAddress -> {
            postalAddress.stringValue("postCode", "string");
            postalAddress.stringValue("type", "residential");
            postalAddress.stringValue("streetName", "string");
            postalAddress.stringValue("buildingNumber", "string");
            postalAddress.stringValue("townName", "string");
            postalAddress.stringValue("country", "string");
            postalAddress.array("addressLine", addressLine -> addressLine.stringValue("string"));
        });
    }

    private void createServicer(LambdaDslObject account) {
        account.object("servicer", servicer -> {
            servicer.stringValue("name", "string"); //string
            servicer.object("identifier", identifier -> {
                identifier.stringValue("countryOfResidence", "string"); // se over
                identifier.stringValue("value", "string");
                identifier.stringValue("type", "countryIdentificationCode");
            });
            servicer.stringValue("type", "countryIdentificationCode");
        });
    }

    private void createElectronicAdress(LambdaDslObject accountObject, String links2, String rel,
        String string, String href) {
        accountObject.array(links2, links ->
            links.object(electronicAddressObject -> {
                electronicAddressObject.stringValue(rel, string);
                electronicAddressObject.stringValue(href, "string");
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
