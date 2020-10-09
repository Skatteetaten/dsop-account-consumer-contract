package dsop.konsument.kontrakt;

import static org.assertj.core.api.Assertions.assertThat;

import static dsop.konsument.kontrakt.util.TestUtils.getResposeFromFile;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccount;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccountDetails;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalCards;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalRoles;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalTransactions;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.text.ParseException;
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
import dsop.konsument.kontrakt.util.AccountDetailsCreator;
import dsop.konsument.kontrakt.util.AccountListCreator;
import dsop.konsument.kontrakt.util.CardsCreator;
import dsop.konsument.kontrakt.util.EmptyAccountListCreator;
import dsop.konsument.kontrakt.util.ErrorCreator;
import dsop.konsument.kontrakt.util.RolesCreator;
import dsop.konsument.kontrakt.util.TransactionsCreator;
import net.javacrumbs.jsonunit.core.Option;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

public class ConsumerContractTest {
    private static final Logger LOGGER = Logger.getLogger(ConsumerContractTest.class.getName());
    private TransactionsCreator transactionsCreator;
    private RolesCreator rolesCreator;
    private AccountListCreator accountListCreator;
    private CardsCreator cardsCreator;
    private AccountDetailsCreator accountDetailsCreator;
    private ErrorCreator errorCreator;
    private EmptyAccountListCreator emptyAccountListCreator;

    private static final String PARTY_ID_HEADER = "PartyID";

    private static final String LEGAL_MANDATE_HEADER = "Legal-Mandate";
    private static final String CORRELATION_ID_HEADER = "CorrelationID";
    private static final String INFO_REQUEST_ID_HEADER = "AccountInfoRequestID";

    private static final String PARTY_ID = "909716212";
    private static final String PARTY_ID_2 = "124678913";
    private static final String PARTY_ID_NO_ACCOUNTS = "123456789";
    private static final String LEGAL_MANDATE = "Skatteforvaltningsloven%20%C2%A7%2010-2";
    private static final String LEGAL_MANDATE_ERROR = "ERROR";
    private static final String CORRELATION_ID_ACCOUNT_LIST = "14aea0c2-0742-4b84-8ac9-0844d05d4673";
    private static final String CORRELATION_ID_ACCOUNT_LIST_EMPTY = "fa9d1bcf-e6f5-47ec-95b8-37e47e2d0868";
    private static final String CORRELATION_ID_ACCOUNT_DETAILS = "5cba4c0d-afc3-4e96-b6c8-e9de2e81a31d";
    private static final String CORRELATION_ID_CARDS = "c049c99f-8a60-41ea-90c9-1889471394d0";
    private static final String CORRELATION_ID_ROLES = "8981b032-bdc2-4a01-a9d4-f0e5d938cce9";
    private static final String CORRELATION_ID_TRANSACTIONS = "b2e25cd6-8bb6-40c1-9aa8-29d7ca114cb3";
    private static final String INFO_REQUEST_ID = "964261f0-1eb3-4c7b-b5e6-99f40ba6ae4d";

    private static final String AUTHORIZATION = "Bearer eyJraWQiOiJjWmswME1rbTVIQzRnN3Z0NmNwUDVGSFpMS0pzdzhmQkFJdUZiUzRSVEQ0IiwiYWxn"
        + "IjoiUlMyNTYifQ.eyJhdWQiOiJodHRwczpcL1wvdGVzdC5wdWJsaWNzZWN0b3IuZG5iLm5vXC92MVwvIiwic2NvcGUiOiJiaXRzOmtvbnRvaW5mb3JtYXNqb2"
        + "4iLCJpc3MiOiJodHRwczpcL1wvb2lkYy12ZXIyLmRpZmkubm9cL2lkcG9ydGVuLW9pZGMtcHJvdmlkZXJcLyIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJleHA"
        + "iOjE1NzEzMTE3NTIsImlhdCI6MTU3MTMxMTYzMiwiY2xpZW50X2lkIjoiNTg3ZjFlMTMtNjJkMS00ODgwLThlZmItNTBiZjIxYTVhYWM5IiwiY2xpZW50X29y"
        + "Z25vIjoiOTc0NzYxMDc2IiwianRpIjoidE83bDBOZWNUel9PMTFfOFJKYU40bGJXaW5zZ09JeS14dWUzbWJNd1pDdyIsImNvbnN1bWVyIjp7ImF1dGhvcml0e"
        + "SI6ImlzbzY1MjMtYWN0b3JpZC11cGlzIiwiSUQiOiIwMTkyOjk3NDc2MTA3NiJ9fQ.s-xpZYFd7indeAyDet8eph3DxrK4AErNHkoDvOps7kaU5OVDrHxHDEK"
        + "JsX5bJjR5J3RfaoRt0QfgorQzfes9BmnSVleGjxwkhcsY32K17Q78dRar1RlRzgHlKUE3x6x0mf-N0DXJc2-vx6OeUxn2BDZADo9-n8deRalXzj0mX8NG9Jds"
        + "ZKH-WjWgFfEm6ekFHIv2lyQZz3govsxLYKahTpMBkjhx2hhaK0OKRGtPP8ggfn0Q3GfUkmMe3S2n1KmFvYVuoTGWmjUWm4r1bJavTR2xknr33i9t9yZXFCJIQ"
        + "W55c78WjcEr5UGijvZ5XVt3IZzUrt8UYtkJtnzYjtvY1w";

    private void setupDslPartCreators() {
        transactionsCreator = new TransactionsCreator();
        rolesCreator = new RolesCreator();
        accountListCreator = new AccountListCreator();
        cardsCreator = new CardsCreator();
        accountDetailsCreator = new AccountDetailsCreator();
        errorCreator = new ErrorCreator();
        emptyAccountListCreator = new EmptyAccountListCreator();
    }

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("bank_provider", "localhost", 8082, PactSpecVersion.V2, this);


    private Map createHeaderMap(String key, String value) {
        Map<String, String> headers = new HashMap<>();
        headers.put(key, value);

        return headers;
    }

    @Pact(consumer = "etat_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws ParseException {
        setupDslPartCreators();
        DslPart pactAccountsBody = accountListCreator.getAccountListDslPart();
        DslPart pactAccountDetailsBody = accountDetailsCreator.getAccountDetailsDslPart();
        DslPart pactCardsBody = cardsCreator.getCardsDslPart();
        DslPart pactRolesBody = rolesCreator.getRolesDslPart();
        DslPart pactTransactionsBody = transactionsCreator.getTransactionsDslPart();
        DslPart pactEmptyAccountsBody = emptyAccountListCreator.getEmptyAccountsDslPart();
        DslPart pactErrorAccountListDsl = errorCreator.getErrorDslPart();

        Map<String, String> Listheaders = new HashMap<>();
        Listheaders.put(PARTY_ID_HEADER, PARTY_ID);
        Listheaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        Listheaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST);
        Listheaders.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);
        Listheaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> EmptyListHeaders = new HashMap<>();
        EmptyListHeaders.put(PARTY_ID_HEADER, PARTY_ID_NO_ACCOUNTS);
        EmptyListHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        EmptyListHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        EmptyListHeaders.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);
        EmptyListHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> WrongParameters = new HashMap<>();
        WrongParameters.put(PARTY_ID_HEADER, PARTY_ID_2);
        WrongParameters.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE_ERROR);
        WrongParameters.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        WrongParameters.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);
        WrongParameters.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> accountDetailsHeaders = new HashMap<>();
        accountDetailsHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        accountDetailsHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_DETAILS);
        accountDetailsHeaders.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);
        accountDetailsHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> cardsHeaders = new HashMap<>();
        cardsHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        cardsHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_CARDS);
        cardsHeaders.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);
        cardsHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

        Map<String, String> rolesHeaders = new HashMap<>();
        rolesHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        rolesHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_ROLES);
        rolesHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        rolesHeaders.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        Map<String, String> transactionsHeaders = new HashMap<>();
        transactionsHeaders.put(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        transactionsHeaders.put(CORRELATION_ID_HEADER, CORRELATION_ID_TRANSACTIONS);
        transactionsHeaders.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        transactionsHeaders.put(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

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
                .path("/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .method("GET")
                .headers(accountDetailsHeaders)
            .willRespondWith()
                .headers(responseHeaders)
                .status(200)
                .body(pactAccountDetailsBody)

            .given("test GET Cards")
                .uponReceiving("GET Cards REQUEST")
                .path("/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/cards")
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
                .path("/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/roles")
                .query("fromDate=2016-12-09&toDate=2016-12-09")
                .method("GET")
            .willRespondWith()
                .headers(responseHeaders)
                .status(200)
                .body(pactRolesBody)

            .given("test GET Transactions")
                .uponReceiving("GET Transactions REQUEST")
                .path("/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/transactions")
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
        accountListHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        emptyAccountListHeaders.set(PARTY_ID_HEADER, PARTY_ID_NO_ACCOUNTS);
        emptyAccountListHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        emptyAccountListHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        emptyAccountListHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        emptyAccountListHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        wrongAccountListHeaders.set(PARTY_ID_HEADER, PARTY_ID_2);
        wrongAccountListHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE_ERROR);
        wrongAccountListHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_LIST_EMPTY);
        wrongAccountListHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        wrongAccountListHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        detailsHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        detailsHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ACCOUNT_DETAILS);
        detailsHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        detailsHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        cardsHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        cardsHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_CARDS);
        cardsHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        cardsHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        rolesHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        rolesHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_ROLES);
        rolesHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        rolesHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

        transactionHeaders.set(LEGAL_MANDATE_HEADER, LEGAL_MANDATE);
        transactionHeaders.set(CORRELATION_ID_HEADER, CORRELATION_ID_TRANSACTIONS);
        transactionHeaders.set(HttpHeaders.AUTHORIZATION, AUTHORIZATION);
        transactionHeaders.set(INFO_REQUEST_ID_HEADER, INFO_REQUEST_ID);

    }

    private void verifyRoles(RestTemplate restTemplate, HttpHeaders accountCommonHeaders) {
        String RolesUrl = mockProvider.getUrl() + "/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/roles?fromDate=2016-12-09&toDate=2016-12-09";
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
        String cardsUrl = mockProvider.getUrl() + "/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/cards?fromDate=2016-12-09&toDate=2016-12-09";
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
            mockProvider.getUrl() + "/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr/transactions?fromDate=2016-12-09&toDate=2016-12-09";
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
        String accountDetailsUrl = mockProvider.getUrl() + "/v1/accounts/MFQ9dT2XYx8_aTNftDCtMbvZacI__3VVyM9ZZBOo4_Zr?fromDate=2016-12-09&toDate=2016-12-09";
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
}
