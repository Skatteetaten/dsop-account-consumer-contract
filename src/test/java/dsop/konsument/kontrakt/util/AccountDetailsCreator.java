package dsop.konsument.kontrakt.util;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.dius.pact.consumer.dsl.DslPart;

public class AccountDetailsCreator {
    private CommonCreator commonCreator;

    public AccountDetailsCreator() {
        this.commonCreator = new CommonCreator();
    }

    public DslPart getAccountDetailsDslPart() throws ParseException {

        Date registered = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2019-05-20T10:23:38");
        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date endDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((accountDetails) -> {
            accountDetails.stringValue("responseStatus", "complete");
            accountDetails.object("account", account -> {
                account.stringValue("status", "enabled"); // enum
                commonCreator.addServicer(account); // se over
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
                commonCreator.addPrimaryOwner(startDate, endDate, account);
            });
        }).build();
    }

}
