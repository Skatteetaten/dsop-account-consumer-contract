package dsop.konsument.kontrakt;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankApi {

    @GetMapping(path = "/accounts", produces = "application/json")
    public String getKontoList(@RequestHeader("Authorization") String authorization,
        @RequestHeader("CorrelationID") String CorrelationID,
        @RequestHeader("PartyID") String PartyID,
        @RequestHeader("Legal-Mandate") String Mandate) throws IOException {
        if ("123456789".equals(PartyID)) {
            return getResposeFromFile("AccountListEmpty.json");
        }
        return getResposeFromFile("AccountList.json");
    }

    @GetMapping(path = "/accounts/5687123451", produces = "application/json")
    public String getKontoDetails(@RequestHeader("Authorization") String authorization,
        @RequestHeader("CorrelationID") String CorrelationID,
        @RequestHeader("Legal-Mandate") String Mandate) throws IOException {
        return getResposeFromFile("AccountDetail.json");
    }

    @GetMapping(path = "/accounts/5687123451/cards", produces = "application/json")
    public String getKontoCards(@RequestHeader("Authorization") String authorization,
        @RequestHeader("CorrelationID") String CorrelationID,
        @RequestHeader("Legal-Mandate") String Mandate) throws IOException {
        return getResposeFromFile("AccountCard.json");
    }

    @GetMapping(path = "/accounts/5687123451/roles", produces = "application/json")
    public String getKontoRoles(@RequestHeader("Authorization") String authorization,
        @RequestHeader("CorrelationID") String CorrelationID,
        @RequestHeader("Legal-Mandate") String Mandate) throws IOException {
        return getResposeFromFile("AccountRoles.json");
    }

    @GetMapping(path = "/accounts/5687123451/transactions", produces = "application/json")
    public String getKonto(@RequestHeader("Authorization") String authorization,
        @RequestHeader("CorrelationID") String CorrelationID,
        @RequestHeader("Legal-Mandate") String Mandate) throws IOException {
        return getResposeFromFile("AccountTransactions.json");
    }

    private static String getResposeFromFile(final String fileName) throws IOException {
        File responseMessage = new ClassPathResource(fileName).getFile();
        return FileUtils.readFileToString(responseMessage, "utf-8");
    }

}
