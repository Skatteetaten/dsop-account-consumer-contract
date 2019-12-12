package dsop.konsument.kontrakt.util;

import static ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.ElectronicAddressType.PHONENUMBER;

import java.util.Date;

import io.pactfoundation.consumer.dsl.LambdaDslObject;

public class CommonCreator {

    void addCardIdentifier(Date startDate, Date expiryDate, LambdaDslObject parentDslObject) {
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

    void addPostalAdress(LambdaDslObject parentDslObject) {
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

    void addPrimaryOwner(Date startDate, Date expiryDate, LambdaDslObject parentDslObject) {
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

    void addServicer(LambdaDslObject account) {
        account.object("servicer", servicer -> {
            servicer.stringValue("name", "DNB");
            servicer.object("identifier", identifier -> {
                identifier.stringValue("countryOfResidence", "NO");
                identifier.stringValue("value", "123456879");
                identifier.stringValue("type", "countryIdentificationCode");
            });
        });
    }

    void addIdentifier(LambdaDslObject parentDslObject) {
        parentDslObject.object("identifier", identifier -> {
            identifier.stringValue("countryOfResidence", "NO"); //ISO standard 2 bokstaver
            identifier.stringValue("value", "10108054242");
            identifier.stringValue("type", "nationalIdentityNumber"); // en
        });

    }

    public void addLinks(LambdaDslObject accountObject, String links2, String rel,
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
