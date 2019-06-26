package dsop.konsument.kontrakt.service;

import org.springframework.stereotype.Service;

import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

@Service
public class AccountsService {

    public Accounts getAccounts(String PartyId) {
        return new Accounts();
    }

    public Cards getCards() {
        return new Cards();
    }

    public Roles getRoles() {
        return new Roles();
    }

    public AccountDetails getAccountDetails() {
        return new AccountDetails();
    }

    public Transactions getTransactions() {
        return new Transactions();
    }
}
