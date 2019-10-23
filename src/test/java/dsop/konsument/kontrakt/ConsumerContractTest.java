package dsop.konsument.kontrakt;

import static org.assertj.core.api.Assertions.assertThat;

import static dsop.konsument.kontrakt.util.TestUtils.getResposeFromFile;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccount;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccountDetails;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalCards;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalRoles;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalTransactions;
import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.ElectronicAddressType.PHONENUMBER;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import io.pactfoundation.consumer.dsl.LambdaDslObject;
import net.javacrumbs.jsonunit.core.Option;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

public class ConsumerContractTest {
    private static final Logger LOGGER = Logger.getLogger(ConsumerContractTest.class.getName());

    private static final String PARTY_ID_HEADER = "PartyID";

    private static final String LEGAL_MANDATE_HEADER = "Legal-Mandate";
    private static final String CORRELATION_ID_HEADER = "CorrelationID";

    private static final String PARTY_ID = "909716212";
    private static final String PARTY_ID_2 = "124678913";
    private static final String PARTY_ID_NO_ACCOUNTS = "123456789";
    private static final String LEGAL_MANDATE = "Skatteforvaltningsloven%20%C2%A7%2010-2%201";
    private static final String LEGAL_MANDATE_ERROR = "ERROR";
    private static final String CORRELATION_ID_ACCOUNT_LIST = "14aea0c2-0742-4b84-8ac9-0844d05d4673";
    private static final String CORRELATION_ID_ACCOUNT_LIST_EMPTY = "fa9d1bcf-e6f5-47ec-95b8-37e47e2d0868";
    private static final String CORRELATION_ID_ACCOUNT_DETAILS = "5cba4c0d-afc3-4e96-b6c8-e9de2e81a31d";
    private static final String CORRELATION_ID_CARDS = "c049c99f-8a60-41ea-90c9-1889471394d0";
    private static final String CORRELATION_ID_ROLES = "8981b032-bdc2-4a01-a9d4-f0e5d938cce9";
    private static final String CORRELATION_ID_TRANSACTIONS = "b2e25cd6-8bb6-40c1-9aa8-29d7ca114cb3";

    private static final String AUTHORIZATION = "Bearer eyJraWQiOiJjWmswME1rbTVIQzRnN3Z0NmNwUDVGSFpMS0pzdzhmQkFJdUZiUzRSVEQ0IiwiYWxn"
        + "IjoiUlMyNTYifQ.eyJhdWQiOiJodHRwczpcL1wvdGVzdC5wdWJsaWNzZWN0b3IuZG5iLm5vXC92MVwvIiwic2NvcGUiOiJiaXRzOmtvbnRvaW5mb3JtYXNqb2"
        + "4iLCJpc3MiOiJodHRwczpcL1wvb2lkYy12ZXIyLmRpZmkubm9cL2lkcG9ydGVuLW9pZGMtcHJvdmlkZXJcLyIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJleHA"
        + "iOjE1NzEzMTE3NTIsImlhdCI6MTU3MTMxMTYzMiwiY2xpZW50X2lkIjoiNTg3ZjFlMTMtNjJkMS00ODgwLThlZmItNTBiZjIxYTVhYWM5IiwiY2xpZW50X29y"
        + "Z25vIjoiOTc0NzYxMDc2IiwianRpIjoidE83bDBOZWNUel9PMTFfOFJKYU40bGJXaW5zZ09JeS14dWUzbWJNd1pDdyIsImNvbnN1bWVyIjp7ImF1dGhvcml0e"
        + "SI6ImlzbzY1MjMtYWN0b3JpZC11cGlzIiwiSUQiOiIwMTkyOjk3NDc2MTA3NiJ9fQ.s-xpZYFd7indeAyDet8eph3DxrK4AErNHkoDvOps7kaU5OVDrHxHDEK"
        + "JsX5bJjR5J3RfaoRt0QfgorQzfes9BmnSVleGjxwkhcsY32K17Q78dRar1RlRzgHlKUE3x6x0mf-N0DXJc2-vx6OeUxn2BDZADo9-n8deRalXzj0mX8NG9Jds"
        + "ZKH-WjWgFfEm6ekFHIv2lyQZz3govsxLYKahTpMBkjhx2hhaK0OKRGtPP8ggfn0Q3GfUkmMe3S2n1KmFvYVuoTGWmjUWm4r1bJavTR2xknr33i9t9yZXFCJIQ"
        + "W55c78WjcEr5UGijvZ5XVt3IZzUrt8UYtkJtnzYjtvY1w";

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("bank_provider", "localhost", 8082, PactSpecVersion.V2, this);

    @Pact(consumer = "etat_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws ParseException {
        DslPart pactAccountsBody = getAccountListDslPart();
        DslPart pactAccountDetailsBody = getAccountDetailsDslPart();
        DslPart pactCardsBody = getCardsDslPart();
        DslPart pactRolesBody = getRolesDslPart();
        DslPart pactTransactionsBody = getTransactionsDslPart();
        DslPart pactEmptyAccountsBody = getEmptyAccountsDslPart();
        DslPart pactErrorAccountListDsl = getErrorDslPart();

        Map<String, String> Listheaders = new HashMap<>();
        Listheaders.put(PARTY_ID_HEADER, PARTY_ID);
        Listheaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        Listheaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST);
        Listheaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> EmptyListHeaders = new HashMap<>();
        EmptyListHeaders.put(PARTY_ID_HEADER, PARTY_ID_NO_ACCOUNTS);
        EmptyListHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        EmptyListHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        EmptyListHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> WrongParameters = new HashMap<>();
        WrongParameters.put(PARTY_ID_HEADER, PARTY_ID_2);
        WrongParameters.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE_ERROR);
        WrongParameters.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        WrongParameters.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> accountDetailsHeaders = new HashMap<>();
        accountDetailsHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        accountDetailsHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_DETAILS);
        accountDetailsHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> cardsHeaders = new HashMap<>();
        cardsHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        cardsHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_CARDS);
        cardsHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> rolesHeaders = new HashMap<>();
        rolesHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        rolesHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ROLES);
        rolesHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> transactionsHeaders = new HashMap<>();
        transactionsHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        transactionsHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_TRANSACTIONS);
        transactionsHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(HttpHeaders.CONTENT_TYPE, "application/json");

        return builder
            .given("test GET AccountList")
                .uponReceiving("GET AccountList REQUEST") // lag beskrivelse av testen
                .path("/v1/accounts")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .headers(Listheaders)
                .method("GET")
            .willRespondWith()
                .headers(responseHeaders)
                .status(200)
                .body(pactAccountsBody)

            .given("test GET AccountDetails")
                .uponReceiving("GET AccountDetails REQUEST")
                .path("/v1/accounts/5687123451")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .method("GET")
                .headers(accountDetailsHeaders)
            .willRespondWith()
                .headers(responseHeaders)
                .status(200)
                .body(pactAccountDetailsBody)

            .given("test GET Cards")
                .uponReceiving("GET Cards REQUEST")
                .path("/v1/accounts/5687123451/cards")
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
                .path("/v1/accounts/5687123451/roles")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .method("GET")
            .willRespondWith()
                .headers(responseHeaders)
                .status(200)
                .body(pactRolesBody)

            .given("test GET Transactions")
                .uponReceiving("GET Transactions REQUEST")
                .path("/v1/accounts/5687123451/transactions")
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
                .path("/v1/accounts")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .method("GET")
                .headers(EmptyListHeaders)
            .willRespondWith()
                .headers(responseHeaders)
                .status(200)
                .body(pactEmptyAccountsBody)

            .given("test GET wrong header AccountList")
                .uponReceiving("GET AccountList with missing header REQUEST")
                .path("/v1/accounts")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .method("GET")
                .headers(WrongParameters)
            .willRespondWith()
                .headers(responseHeaders)
                .status(400)
                .body(pactErrorAccountListDsl)

            .toPact();
    }

    @Test
    @PactVerification()
    public void shouldVerifyProviderResponses() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders accountListHeaders = new HttpHeaders();
        HttpHeaders emptyAccountListHeaders = new HttpHeaders();
        HttpHeaders wrongAccountListHeaders = new HttpHeaders();
        HttpHeaders detailsHeaders = new HttpHeaders();
        HttpHeaders cardsHeaders = new HttpHeaders();
        HttpHeaders rolesHeaders = new HttpHeaders();
        HttpHeaders transactionHeaders = new HttpHeaders();

        createRequestHeaders(accountListHeaders, emptyAccountListHeaders, wrongAccountListHeaders, detailsHeaders, cardsHeaders, rolesHeaders,
            transactionHeaders);

        verifyAccountList(restTemplate, accountListHeaders);
        verifyAccountDetails(restTemplate, detailsHeaders);
        verifyEmptyAccountList(restTemplate, emptyAccountListHeaders);
        verifyWrongParameterAccountList(restTemplate, wrongAccountListHeaders);
        verifyTransactions(restTemplate, transactionHeaders);
        verifyCards(restTemplate, cardsHeaders);
        verifyRoles(restTemplate, rolesHeaders);

    }

    private void createRequestHeaders(HttpHeaders accountListHeaders, HttpHeaders emptyAccountListHeaders, HttpHeaders wrongAccountListHeaders,
        HttpHeaders detailsHeaders, HttpHeaders cardsHeaders, HttpHeaders rolesHeaders,
        HttpHeaders transactionHeaders) {

        accountListHeaders.set(PARTY_ID_HEADER, PARTY_ID);
        accountListHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        accountListHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST);
        accountListHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        emptyAccountListHeaders.set(PARTY_ID_HEADER, PARTY_ID_NO_ACCOUNTS);
        emptyAccountListHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        emptyAccountListHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        emptyAccountListHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        wrongAccountListHeaders.set(PARTY_ID_HEADER, PARTY_ID_2);
        wrongAccountListHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE_ERROR);
        wrongAccountListHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        wrongAccountListHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        detailsHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        detailsHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_DETAILS);
        detailsHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        cardsHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        cardsHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_CARDS);
        cardsHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        rolesHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        rolesHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ROLES);
        rolesHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        transactionHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        transactionHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_TRANSACTIONS);
        transactionHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
    }

    private void verifyRoles(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String RolesUrl = mockProvider.getUrl() + "/v1/accounts/5687123451/roles?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> rolesResponse = sendRequest(restTemplate, accountCommonHeaders, RolesUrl);
        String jsonRoles = rolesResponse.getBody();
        assertThat(rolesResponse.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThatJson(jsonRoles)
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(getResposeFromFile("responses/AccountRoles.json"));

        Roles roles = unmarhalRoles(jsonRoles);
        assertThat(roles).isNotNull();
        assertThat(roles.getRoles()).isNotNull();
    }

    private void verifyCards(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String cardsUrl = mockProvider.getUrl() + "/v1/accounts/5687123451/cards?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> cardsResponse = sendRequest(restTemplate, accountCommonHeaders, cardsUrl);

        String jsonCards = cardsResponse.getBody();

        assertThat(cardsResponse.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThatJson(jsonCards)
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(getResposeFromFile("responses/AccountCard.json"));

        Cards cards = unmarhalCards(jsonCards);
        assertThat(cards).isNotNull();
        assertThat(cards.getPaymentCards()).isNotNull();
        assertThat(cards.getResponseStatus()).isNotNull();
    }

    private void verifyTransactions(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String transactionsUrl =
            mockProvider.getUrl() + "/v1/accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> transactionsResponse = sendRequest(restTemplate, accountCommonHeaders, transactionsUrl);

        String jsonTransactions = transactionsResponse.getBody();
        assertThat(transactionsResponse.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThatJson(jsonTransactions)
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(getResposeFromFile("responses/AccountTransactions.json"));

        Transactions transactions = unmarhalTransactions(jsonTransactions);
        assertThat(transactions).isNotNull();
        assertThat(transactions.getResponseStatus()).isNotNull();
        assertThat(transactions.getTransactions()).isNotNull();
    }


    private void verifyAccountDetails(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountDetailsUrl = mockProvider.getUrl() + "/v1/accounts/5687123451?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountDetailsResponse =
            sendRequest(restTemplate, accountListHeaders, accountDetailsUrl);

        String jsonAccountDetails = accountDetailsResponse.getBody();
        assertThat(accountDetailsResponse.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThatJson(jsonAccountDetails)
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(getResposeFromFile("responses/AccountDetail.json"));

        AccountDetails accountDetails = unmarhalAccountDetails(jsonAccountDetails);
        assertThat(accountDetails).isNotNull();
        assertThat(accountDetails.getAccount()).isNotNull();
        assertThat(accountDetails.getResponseStatus()).isNotNull();
    }

    private void verifyAccountList(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountList = mockProvider.getUrl() + "/v1/accounts?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountsResponse =
            sendRequest(restTemplate, accountListHeaders, accountList);

        String jsonAccounts = accountsResponse.getBody();
        assertThat(accountsResponse.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThatJson(jsonAccounts)
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(getResposeFromFile("responses/AccountList.json"));

        Accounts accounts = unmarhalAccount(jsonAccounts);
        assertThat(accounts).isNotNull();
        assertThat(accounts.getAccounts()).isNotNull();
    }

    private void verifyEmptyAccountList(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountList = mockProvider.getUrl() + "/v1/accounts?fromDate=2016-12-09&toDate=2016-12-09";
        ResponseEntity<String> accountsResponse =
            sendRequest(restTemplate, accountListHeaders, accountList);
        String jsonAccounts = accountsResponse.getBody();

        assertThat(accountsResponse.getStatusCode()).isSameAs(HttpStatus.OK);
        assertThatJson(jsonAccounts)
            .when(Option.IGNORING_ARRAY_ORDER)
            .isEqualTo(getResposeFromFile("responses/AccountListEmpty.json"));

        Accounts accounts = unmarhalAccount(jsonAccounts);
        assertThat(accounts).isNotNull();
    }

    private void verifyWrongParameterAccountList(RestTemplate restTemplate, HttpHeaders accountListHeaders) {
        String accountList = mockProvider.getUrl() + "/v1/accounts?fromDate=2016-12-09&toDate=2016-12-09";
        try {
            ResponseEntity<String> accountsResponse =
                sendRequest(restTemplate, accountListHeaders, accountList);
        } catch (HttpClientErrorException e ) {
            assertThat(e).hasMessage("400 Bad Request");
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
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
                        balanceObject.numberValue("amount", 20.2); // type så lenge det er med 2 desimaler
                        balanceObject.stringValue("creditDebitIndicator", "credit"); // enum
                        balanceObject.date("registered", "yyyy-MM-dd'T'HH:mm:ss", registered);
                        balanceObject.stringValue("type", "availableBalance"); //enum
                        balanceObject.numberValue("creditLineAmount", 10.9); //se over
                        balanceObject.stringValue("creditLineCurrency", "NOK"); //se over
                        balanceObject.booleanValue("creditLineIncluded", false); //se over
                    }));
                addPrimaryOwner(startDate, endDate, account);
            });
        }).build();
    }

    private DslPart getAccountListDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2017-05-20");

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
                accountObject.stringValue("accountReference",
                    "MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr"); //.* string -> StringType
                accountObject.stringValue("type", "loanAccount"); //stringMatcher test mot alle verdier
                accountObject.stringValue("currency", "NOK"); // Stringmatcher Uppercase 3 letters A-Z
                addPrimaryOwner(startDate, expiryDate, accountObject);
                accountObject.stringValue("name", "Boomsma Erika");
            }));
        }).build();
    }

    private DslPart getErrorDslPart() throws ParseException {
        return newJsonBody((error) -> {
            error.stringValue("code", "ACC-001");
            error.stringValue("message", "ACC-001 Bad request. Ugyldige parametere i forespørselen");
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
        Date expiryDate = new SimpleDateFormat("yyyy-mm").parse("2017-05");

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
                roleObject.object("postalAddress", postalAddress -> {
                    postalAddress.stringValue("postCode", "1598");
                    postalAddress.stringValue("type", "residential");
                    postalAddress.stringValue("streetName", "trysilgata");
                    postalAddress.stringValue("buildingNumber", "2");
                    postalAddress.stringValue("townName", "Oslo");
                    postalAddress.stringValue("country", "NO");
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
        Date expiryDate = new SimpleDateFormat("yyyy-mm").parse("2017-05");

        return newJsonBody((transactionsBody) -> {
            transactionsBody.stringValue("responseStatus", "complete");
            transactionsBody.array("transactions", transactions -> transactions.object(transactionsObject -> {
                transactionsObject.date("bookingDate", "yyyy-MM-dd'T'HH:mm:ss", bookingDate);
                transactionsObject.date("valueDate", "yyyy-MM-dd'T'HH:mm:ss", valueDate);
                transactionsObject.stringValue("transactionIdentifier", "DSOP10000000318308");
                transactionsObject.booleanType("reversalIndicator", true);
                transactionsObject.stringValue("status", "booked");
                transactionsObject.date("registered", "yyyy-MM-dd'T'HH:mm:ss", registredDate);
                transactionsObject.numberValue("amount", 100.34);
                transactionsObject.stringValue("creditDebitIndicator", "credit"); // enum
                transactionsObject.stringValue("currency", "NOK");
                transactionsObject.stringValue("additionalInfo", "info");
                transactionsObject.stringValue("merchant", "Power");
                transactionsObject
                    .object("paymentCard", paymentCard -> addCardIdentifier(startDate, expiryDate, paymentCard));
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
            }));
            transactionsBody.array("links", links -> links.object(link -> {
                link.stringValue("rel", "next");
                link.stringType("href", "/accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09&page=0");
            }));

        }).build();
    }

    private void addCardIdentifier(Date startDate, Date expiryDate, LambdaDslObject parentDslObject) {
        parentDslObject.stringValue("holderName", "Alma"); // String
        parentDslObject.stringValue("cardIssuerName", "Sparebanken AS"); //String
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
            postalAddress.stringValue("country", "NO");
            postalAddress.array("addressLines", addressLine -> addressLine.stringValue("bondes vei 4"));
        });
    }

    private void addPrimaryOwner(Date startDate, Date expiryDate, LambdaDslObject parentDslObject) {
        parentDslObject.object("primaryOwner", primaryOwner -> {
            primaryOwner.stringValue("permission", "rightToUseAlone"); //StringMatch enums
            addIdentifier(primaryOwner);
            primaryOwner.stringValue("name", "Boomsma Erika"); // String
            primaryOwner.date("startDate", "yyyy-mm-dd", startDate);
            primaryOwner.date("endDate", "yyyy-mm-dd", expiryDate);
            addElectronicAdress(primaryOwner, "electronicAddresses", PHONENUMBER.toString(), "96711125");
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

    private void addElectronicAdress(LambdaDslObject primaryOwner, String arrayName, String type, String value) {
        primaryOwner.array(arrayName, links ->
            links.object(electronicAddressObject -> {
                electronicAddressObject.stringType("type", type);
                electronicAddressObject.stringType("value", value);
            }));
    }
}
