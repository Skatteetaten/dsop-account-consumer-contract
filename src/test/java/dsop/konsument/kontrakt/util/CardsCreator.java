package dsop.konsument.kontrakt.util;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.dius.pact.consumer.dsl.DslPart;

public class CardsCreator {
    private CommonCreator commonCreator;

    public CardsCreator() {
        this.commonCreator = new CommonCreator();
    }

    public DslPart getCardsDslPart() throws ParseException {

        Date startDate = new SimpleDateFormat("yyyy-mm").parse("2010-05");
        Date expiryDate = new SimpleDateFormat("yyyy-mm").parse("2017-05");

        return newJsonBody((cardsBody) -> {
            cardsBody.stringValue("responseStatus", "complete");
            cardsBody.array("paymentCards", paymentCards -> paymentCards.object(cardObject -> {
                commonCreator.addCardIdentifier(startDate, expiryDate, cardObject);
            }));
        }).build();
    }
}
