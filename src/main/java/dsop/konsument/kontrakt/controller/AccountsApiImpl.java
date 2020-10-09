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
    private static final String LEGAL_MANDATE = "Skatteforvaltningsloven%20%C2%A7%2010-2";

    @Autowired
    public AccountsApiImpl(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    public ResponseEntity<Accounts> listAccounts(
        @ApiParam(value = "Unique reference number / case number that follows the case throughout the different requests." ,required=true)
        @RequestHeader(value="AccountInfoRequestID", required=true) UUID accountInfoRequestID,
        @ApiParam(value = "Correlation ID, unique identifier for the technical request" ,required=true)
        @RequestHeader(value="CorrelationID", required=true) UUID correlationID,
        @ApiParam(value = "The Legal basis used by data consumers in order to fetch data. Should be validated by the data provider." ,required=true)
        @RequestHeader(value="Legal-Mandate", required=true) String legalMandate,
        @ApiParam(value = "Parts identifier, personal identification number, D-number or organization number" )
        @RequestHeader(value="PartyID", required=false) String partyID,
        @ApiParam(value = "The account number. Not in use per now." )
        @RequestHeader(value="AccountID", required=false) String accountID,
        @ApiParam(value = "Reference ID based on AdditionalReferenceIDType. Should be validated according to the legal-mandate." )
        @RequestHeader(value="AdditionalReferenceID", required=false) String additionalReferenceID,
        @ApiParam(value = "What type of reference to expect in AdditionalReferenceID" , allowableValues="pol")
        @RequestHeader(value="AdditionalReferenceIDType", required=false) String additionalReferenceIDType,
        @ApiParam(value = "From date, current date if not stated")
        @Valid @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "To date, current date if not stated")
        @Valid @RequestParam(value = "toDate", required = false) LocalDate toDate) {


        Accounts accounts = accountsService.getAccounts(partyID);
        if (legalMandate.equals(LEGAL_MANDATE)) {
            return ResponseEntity.ok(accounts);
        }
        else {
            throw new IllegalArgumentException("Missing legalMandate");
        }
    }

    public ResponseEntity<Cards> listCards(
        @ApiParam(value = "Unique reference to the account. Shall not match the account number.",required=true)
        @PathVariable("accountReference") String accountReference,
        @ApiParam(value = "Unique reference number / case number that follows the case throughout the different requests." ,required=true)
        @RequestHeader(value="AccountInfoRequestID", required=true) UUID accountInfoRequestID,
        @ApiParam(value = "Correlation ID, unique identifier for the technical request" ,required=true)
        @RequestHeader(value="CorrelationID", required=true) UUID correlationID,
        @ApiParam(value = "The Legal basis used by data consumers in order to fetch data. Should be validated by the data provider." ,required=true)
        @RequestHeader(value="Legal-Mandate", required=true) String legalMandate,
        @ApiParam(value = "Reference ID based on AdditionalReferenceIDType. Should be validated according to the legal-mandate." )
        @RequestHeader(value="AdditionalReferenceID", required=false) String additionalReferenceID,
        @ApiParam(value = "What type of reference to expect in AdditionalReferenceID" , allowableValues="pol")
        @RequestHeader(value="AdditionalReferenceIDType", required=false) String additionalReferenceIDType,
        @ApiParam(value = "From date, current date if not stated")
        @Valid @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "To date, current date if not stated")
        @Valid @RequestParam(value = "toDate", required = false) LocalDate toDate) {
        Cards cards = accountsService.getCards();
        return ResponseEntity.ok(cards);
    }

    public ResponseEntity<Roles> listRoles(
        @ApiParam(value = "Unique reference to the account. Shall not match the account number.",required=true)
        @PathVariable("accountReference") String accountReference,
        @ApiParam(value = "Unique reference number / case number that follows the case throughout the different requests." ,required=true)
        @RequestHeader(value="AccountInfoRequestID", required=true) UUID accountInfoRequestID,
        @ApiParam(value = "Correlation ID, unique identifier for the technical request" ,required=true)
        @RequestHeader(value="CorrelationID", required=true) UUID correlationID,
        @ApiParam(value = "The Legal basis used by data consumers in order to fetch data. Should be validated by the data provider." ,required=true)
        @RequestHeader(value="Legal-Mandate", required=true) String legalMandate,
        @ApiParam(value = "Reference ID based on AdditionalReferenceIDType. Should be validated according to the legal-mandate." )
        @RequestHeader(value="AdditionalReferenceID", required=false) String additionalReferenceID,
        @ApiParam(value = "What type of reference to expect in AdditionalReferenceID" , allowableValues="pol")
        @RequestHeader(value="AdditionalReferenceIDType", required=false) String additionalReferenceIDType,
        @ApiParam(value = "From date, current date if not stated")
        @Valid @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "To date, current date if not stated")
        @Valid @RequestParam(value = "toDate", required = false) LocalDate toDate) {
        Roles roles = accountsService.getRoles();
        return ResponseEntity.ok(roles);
    }

    public ResponseEntity<AccountDetails> showAccountById(
        @ApiParam(value = "Unique reference to the account. Shall match the account number.",required=true)
        @PathVariable("accountReference") String accountReference,
        @ApiParam(value = "Unique reference number / case number that follows the case throughout the different requests." ,required=true)
        @RequestHeader(value="AccountInfoRequestID", required=true) UUID accountInfoRequestID,
        @ApiParam(value = "Correlation ID, unique identifier for the technical request" ,required=true)
        @RequestHeader(value="CorrelationID", required=true) UUID correlationID,
        @ApiParam(value = "The Legal basis used by data consumers in order to fetch data. Should be validated by the data provider." ,required=true)
        @RequestHeader(value="Legal-Mandate", required=true) String legalMandate,
        @ApiParam(value = "Reference ID based on AdditionalReferenceIDType. Should be validated according to the legal-mandate." )
        @RequestHeader(value="AdditionalReferenceID", required=false) String additionalReferenceID,
        @ApiParam(value = "What type of reference to expect in AdditionalReferenceID" , allowableValues="pol")
        @RequestHeader(value="AdditionalReferenceIDType", required=false) String additionalReferenceIDType,
        @ApiParam(value = "From date, current date if not stated")
        @Valid @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "To date, current date if not stated")
        @Valid @RequestParam(value = "toDate", required = false) LocalDate toDate) {
        AccountDetails accountDetails = accountsService.getAccountDetails();
        return ResponseEntity.ok(accountDetails);
    }

    public ResponseEntity<Transactions> listTransactions(
        @ApiParam(value = "Unique reference to the account. Shall match the account number.",required=true)
        @PathVariable("accountReference") String accountReference,
        @ApiParam(value = "Unique reference number / case number that follows the case throughout the different requests." ,required=true)
        @RequestHeader(value="AccountInfoRequestID", required=true) UUID accountInfoRequestID,
        @ApiParam(value = "Correlation ID, unique identifier for the technical request" ,required=true)
        @RequestHeader(value="CorrelationID", required=true) UUID correlationID,
        @ApiParam(value = "The Legal basis used by data consumers in order to fetch data. Should be validated by the data provider." ,required=true)
        @RequestHeader(value="Legal-Mandate", required=true) String legalMandate,
        @ApiParam(value = "Reference ID based on AdditionalReferenceIDType. Should be validated according to the legal-mandate." )
        @RequestHeader(value="AdditionalReferenceID", required=false) String additionalReferenceID,
        @ApiParam(value = "What type of reference to expect in AdditionalReferenceID" , allowableValues="pol")
        @RequestHeader(value="AdditionalReferenceIDType", required=false) String additionalReferenceIDType,
        @ApiParam(value = "From date, current date if not stated")
        @Valid @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
        @ApiParam(value = "To date, current date if not stated")
        @Valid @RequestParam(value = "toDate", required = false) LocalDate toDate) {
        Transactions transactions = accountsService.getTransactions();
        return ResponseEntity.ok(transactions);
    }
}
