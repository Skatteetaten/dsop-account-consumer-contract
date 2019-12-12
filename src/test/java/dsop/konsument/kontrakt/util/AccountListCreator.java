package dsop.konsument.kontrakt.util;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.dius.pact.consumer.dsl.DslPart;

public class AccountListCreator {

    private CommonCreator commonCreator;

    public AccountListCreator() {
        this.commonCreator = new CommonCreator();
    }

    public DslPart getAccountListDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date expiryDate = new SimpleDateFormat("yyyy-mm-dd").parse("2017-05-20");

        return newJsonBody((accountsList) -> {
            accountsList.stringValue("responseStatus", "complete");
            accountsList.array("accounts", accounts -> accounts.object(accountObject -> {
                accountObject.stringValue("status", "enabled");
                commonCreator.addServicer(accountObject);

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
                commonCreator.addPrimaryOwner(startDate, expiryDate, accountObject);
                accountObject.stringValue("name", "Boomsma Erika");
            }));
        }).build();
    }
}
