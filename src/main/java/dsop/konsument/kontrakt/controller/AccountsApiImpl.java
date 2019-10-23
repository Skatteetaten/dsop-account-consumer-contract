package dsop.konsument.kontrakt.controller;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dsop.konsument.kontrakt.service.AccountsService;
import io.swagger.annotations.ApiParam;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountsApi;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

@RestController
public class AccountsApiImpl implements AccountsApi {

    private AccountsService accountsService;
    private static final String LEGAL_MANDATE = "Skatteforvaltningsloven%20%C2%A7%2010-2%201";

    @Autowired
    public AccountsApiImpl(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

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

        Accounts accounts = accountsService.getAccounts(partyID);
        if (legalMandate.equals(LEGAL_MANDATE)) {
            return ResponseEntity.ok(accounts);
        }
        else {
            throw new IllegalArgumentException("Missing legalMandate");
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

        Cards cards = accountsService.getCards();
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

        Roles roles = accountsService.getRoles();
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

        AccountDetails accountDetails = accountsService.getAccountDetails();
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

        Transactions transactions = accountsService.getTransactions();
        return ResponseEntity.ok(transactions);
    }
}
