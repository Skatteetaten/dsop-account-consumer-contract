package dsop.konsument.kontrakt.controller;

import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccount;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalAccountDetails;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalCards;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalRoles;
import static dsop.konsument.kontrakt.util.Unmarshaller.unmarhalTransactions;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.annotations.ApiParam;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountsApi;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

@RestController
public class AccountsApiImpl implements AccountsApi {
    private static final Logger LOGGER = Logger.getLogger(AccountsApi.class.getName());


    @Override
    public ResponseEntity<Accounts> listAccounts(
        @ApiParam(value = "Korrelasjonsid, unik identifikator for den tekniske forespørselen", required = true)
        @RequestHeader(value = "CorrelationID", required = true)
            UUID correlationID, @ApiParam(value = "Lovhjemmel", required = true)
    @RequestHeader(value = "Legal-Mandate", required = true) String legalMandate,
        @ApiParam(value = "Fra dato, dagens dato dersom ikke oppgitt") @Valid
        @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "Til dato, dagens dato dersom ikke oppgitt") @Valid
        @RequestParam(value = "toDate", required = false) LocalDate toDate,
        @ApiParam(value = "Partsidentifikator, fødselsnummer, d-nummer eller organisasjonsnummer.")
        @RequestHeader(value = "PartyID", required = false) String partyID, @ApiParam(value = "Kontonummeret")
    @RequestHeader(value = "AccountID", required = false) String accountID) {

        if ("123456789".equals(partyID)) {
            String emptyResponseList = getResposeFromFile("AccountListEmpty.json");
            Accounts accounts = unmarhalAccount(emptyResponseList);

            return ResponseEntity.ok(accounts);
        } else {
            String responseList = getResposeFromFile("AccountList.json");
            Accounts accounts = unmarhalAccount(responseList);
            return ResponseEntity.ok(accounts);
        }
    }

    @Override
    public ResponseEntity<Cards> listCards(
        @ApiParam(value = "Unik referanse til kontoen", required = true) @PathVariable("accountReference")
            String accountReference,
        @ApiParam(value = "Korrelasjonsid, unik identifikator for den tekniske forespørselen", required = true)
        @RequestHeader(value = "CorrelationID", required = true) UUID correlationID,
        @ApiParam(value = "Lovhjemmel", required = true) @RequestHeader(value = "Legal-Mandate", required = true)
            String legalMandate, @ApiParam(value = "Fra dato, dagens dato dersom ikke oppgitt") @Valid
    @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "Til dato, dagens dato dersom ikke oppgitt") @Valid
        @RequestParam(value = "toDate", required = false) LocalDate toDate) {

        String cardsResponse = getResposeFromFile("AccountCard.json");
        Cards cards = unmarhalCards(cardsResponse);
        return ResponseEntity.ok(cards);
    }

    @Override
    public ResponseEntity<Roles> listRoles(
        @ApiParam(value = "Unik referanse til kontoen", required = true) @PathVariable("accountReference")
            String accountReference,
        @ApiParam(value = "Korrelasjonsid, unik identifikator for den tekniske forespørselen", required = true)
        @RequestHeader(value = "CorrelationID", required = true) UUID correlationID,
        @ApiParam(value = "Lovhjemmelen", required = true) @RequestHeader(value = "Legal-Mandate", required = true)
            String legalMandate, @ApiParam(value = "Fra dato, dagens dato dersom ikke oppgitt") @Valid
    @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "Til dato, dagens dato dersom ikke oppgitt") @Valid
        @RequestParam(value = "toDate", required = false) LocalDate toDate) {

        String rolesResponse = getResposeFromFile("AccountRoles.json");
        Roles roles = unmarhalRoles(rolesResponse);
        return ResponseEntity.ok(roles);
    }

    @Override
    public ResponseEntity<AccountDetails> showAccountById(
        @ApiParam(value = "Korrelasjonsid, unik identifikator for den tekniske forespørselen", required = true)
        @RequestHeader(value = "CorrelationID", required = true) UUID correlationID,
        @ApiParam(value = "Legal mandate", required = true) @RequestHeader(value = "Legal-Mandate", required = true)
            String legalMandate,
        @ApiParam(value = "Unik referanse til kontoen", required = true) @PathVariable("accountReference")
            String accountReference, @ApiParam(value = "Fra dato, dagens dato dersom ikke oppgitt") @Valid
    @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "Til dato, dagens dato dersom ikke oppgitt") @Valid
        @RequestParam(value = "toDate", required = false) LocalDate toDate) {

        String accountDetailsResponse = getResposeFromFile("AccountDetail.json");
        AccountDetails accountDetails = unmarhalAccountDetails(accountDetailsResponse);
        return ResponseEntity.ok(accountDetails);
    }

    public ResponseEntity<Transactions> listTransactions(
        @ApiParam(value = "Unik referanse til kontoen", required = true) @PathVariable("accountReference")
            String accountReference,
        @ApiParam(value = "Korrelasjonsid, unik identifikator for den tekniske forespørselen", required = true)
        @RequestHeader(value = "CorrelationID", required = true) UUID correlationID,
        @ApiParam(value = "Lovhjemmel", required = true) @RequestHeader(value = "Legal-Mandate", required = true)
            String legalMandate, @ApiParam(value = "Fra dato, dagens dato dersom ikke oppgitt") @Valid
    @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "Til dato, dagens dato dersom ikke oppgitt") @Valid
        @RequestParam(value = "toDate", required = false) LocalDate toDate) {

        String transactionsResponse = getResposeFromFile("AccountTransactions.json");
        Transactions transactions = unmarhalTransactions(transactionsResponse);
        return ResponseEntity.ok(transactions);
    }

    private static String getResposeFromFile(final String fileName) {
        File responseMessage = null;
        try {
            responseMessage = new ClassPathResource(fileName).getFile();
        } catch (IOException e) {
            LOGGER.info("Error while finding file");
        }
        try {
            return FileUtils.readFileToString(responseMessage, "utf-8");
        } catch (IOException e) {
            LOGGER.info("Error while reading file");
        }
        return null;
    }

}
