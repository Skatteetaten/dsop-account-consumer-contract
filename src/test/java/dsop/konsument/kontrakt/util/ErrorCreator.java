package dsop.konsument.kontrakt.util;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

import java.text.ParseException;

import au.com.dius.pact.consumer.dsl.DslPart;

public class ErrorCreator {
    public DslPart getErrorDslPart() throws ParseException {
        return newJsonBody((error) -> {
            error.stringValue("code", "ACC-001");
            error.stringValue("message", "Bad request. Ugyldige parametere i forespørselen");
        }).build();
    }

}
