package dsop.konsument.kontrakt.util;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountDetails;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Accounts;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Cards;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Roles;
import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Transactions;

public class Unmarshaller {
    public static Accounts unmarhalAccount(String kontolisteJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Accounts accountRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            accountRoot = objectMapper.readValue(kontolisteJson, Accounts.class);
            return accountRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Kontoliste kan ikke unmarshalles", e);
        }
    }

    public static AccountDetails unmarhalAccountDetails(String kontodetaljerJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AccountDetails accountDetailsRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            accountDetailsRoot = objectMapper.readValue(kontodetaljerJson, AccountDetails.class);
            return accountDetailsRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Kontodetaljer kan ikke unmarshalles", e);
        }
    }

    public static Cards unmarhalCards(String cardsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Cards cardsRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            cardsRoot = objectMapper.readValue(cardsJson, Cards.class);
            return cardsRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Cards kan ikke unmarshalles", e);
        }
    }

    public static Roles unmarhalRoles(String rolesJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Roles rolesRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            rolesRoot = objectMapper.readValue(rolesJson, Roles.class);
            return rolesRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Cards kan ikke unmarshalles", e);
        }
    }

    public static Transactions unmarhalTransactions(String transactionsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Transactions transactionsRoot;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            transactionsRoot = objectMapper.readValue(transactionsJson, Transactions.class);
            return transactionsRoot;
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("Transactions kan ikke unmarshalles", e);
        }
    }
}
