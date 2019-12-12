package dsop.konsument.kontrakt.util;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.dius.pact.consumer.dsl.DslPart;

public class RolesCreator {

    private CommonCreator commonCreator;

    public RolesCreator() {
        commonCreator = new CommonCreator();
    }

    public DslPart getRolesDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");
        Date endDate = new SimpleDateFormat("yyyy-mm-dd").parse("2010-05-20");

        return newJsonBody((rolesBody) -> {
            rolesBody.stringValue("responseStatus", "complete"); //enum
            rolesBody.array("roles", roles -> roles.object(roleObject -> {
                roleObject.stringValue("name", "Boomsma Erika"); //string
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
                commonCreator.addIdentifier(roleObject);
                roleObject.array("electronicAddresses", electronicAddress ->
                    electronicAddress.object(electronicAddressObject -> {
                        electronicAddressObject.stringValue("type", "emailAddress");
                        electronicAddressObject.stringValue("value", "test@test.no");
                    }));
                roleObject.stringValue("permission", "rightToUseAlone");
            }));
        }).build();
    }
}
