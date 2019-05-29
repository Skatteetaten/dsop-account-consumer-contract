package dsop.konsument.kontrakt;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;

@RunWith(SpringRestPactRunner.class)
@Provider("bank_provider")
@PactFolder("mypacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8080")
public class ProviderIntegrationTest {

    @SuppressWarnings("unused")
    @TestTarget
    public final Target target = new SpringBootHttpTarget();

    @State("test GET AccountList")
    public void getAccountList() {
    }

    @State("test GET AccountDetails")
    public void getAccoutDetails() {
    }

    @State("test GET Cards")
    public void getCards() {
    }

    @State("test GET Roles")
    public void getRoles() {
    }

    @State("test GET Transactions")
    public void getTransactions() {
    }

    @State("test GET empty AccountList")
    public void getEmptyAccountList() {
    }

}
