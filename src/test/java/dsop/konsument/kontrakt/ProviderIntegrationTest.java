package dsop.konsument.kontrakt;

import static org.mockito.Mockito.doReturn;

import static dsop.konsument.kontrakt.util.TestUtils.getResposeFromFile;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccount;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccountDetails;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalCards;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalRoles;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalTransactions;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import dsop.konsument.kontrakt.service.AccountsService;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

@RunWith(SpringRestPactRunner.class)
@Provider("bank_provider")
@PactFolder("mypacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8080")
public class ProviderIntegrationTest {

    @MockBean
    private AccountsService accountsService;

    @SuppressWarnings("unused")
    @TestTarget
    public final Target target = new SpringBootHttpTarget();

    @State("test GET AccountList")
    public void getAccountList() {
        String responseList = getResposeFromFile("responses/AccountList.json");
        Accounts accounts = unmarhalAccount(responseList);
        doReturn(accounts).when(accountsService).getAccounts("909716212");
    }

    @State("test GET AccountDetails")
    public void getAccoutDetails() {
        String accountDetailsResponse = getResposeFromFile("responses/AccountDetail.json");
        AccountDetails accountDetails = unmarhalAccountDetails(accountDetailsResponse);
        doReturn(accountDetails).when(accountsService).getAccountDetails();
    }

    @State("test GET Cards")
    public void getCards() {
        String cardsResponse = getResposeFromFile("responses/AccountCard.json");
        Cards cards = unmarhalCards(cardsResponse);
        doReturn(cards).when(accountsService).getCards();
    }

    @State("test GET Roles")
    public void getRoles() {
        String rolesResponse = getResposeFromFile("responses/AccountRoles.json");
        Roles roles =  unmarhalRoles(rolesResponse);
        doReturn(roles).when(accountsService).getRoles();
    }

    @State("test GET Transactions")
    public void getTransactions() {
        String transactionsResponse = getResposeFromFile("responses/AccountTransactions.json");
        Transactions transactions = unmarhalTransactions(transactionsResponse);
        doReturn(transactions).when(accountsService).getTransactions();
    }

    @State("test GET empty AccountList")
    public void getEmptyAccountList() {
        String emptyResponseList = getResposeFromFile("responses/AccountListEmpty.json");
        Accounts emptyAccounts = unmarhalAccount(emptyResponseList);
        doReturn(emptyAccounts).when(accountsService).getAccounts("123456789");
    }

    @State("test GET wrong header AccountList")
    public void getErrorOnMissingAccountList() {
        // exception is triggered in AccountsApiImpl.class, if Legal-Mandate is wrong.
    }
}
