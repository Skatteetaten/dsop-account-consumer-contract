package dsop.konsument.kontrakt.util;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.dius.pact.consumer.dsl.DslPart;

public class TransactionsCreator {
    private CommonCreator commonCreator;

    public TransactionsCreator() {
        commonCreator = new CommonCreator();
    }

    public DslPart getTransactionsDslPart() throws ParseException {
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
                    .object("paymentCard", paymentCard -> commonCreator.addCardIdentifier(startDate, expiryDate, paymentCard));
                transactionsObject.object("transactionCode", transactionCode -> {
                    transactionCode.stringValue("domain", "accountManagement");
                    transactionCode.stringValue("family", "additionalMiscellaneousCreditOperations");
                    transactionCode.stringValue("subFamily", "valueDate"); // sjekke
                    transactionCode.stringValue("freeText", "VISA Varekjøp");
                });
                transactionsObject.array("counterParties", counterParty ->
                    counterParty.object(counterPartyObject -> {
                        commonCreator.addIdentifier(counterPartyObject);
                        counterPartyObject.stringValue("accountIdentifier", "9867123111");
                        counterPartyObject.stringValue("name", "Selskapet AS");
                        counterPartyObject.stringValue("type", "creditor");
                        commonCreator.addPostalAdress(counterPartyObject);
                    }));
            }));
            transactionsBody.array("links", links -> links.object(link -> {
                link.stringValue("rel", "next");
                link.stringType("href", "/accounts/5687123451/transactions?fromDate=2016-12-09&toDate=2016-12-09");
            }));

        }).build();
    }

}
