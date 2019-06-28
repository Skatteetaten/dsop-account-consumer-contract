package dsop.konsument.kontrakt.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.AccountsApi;

public class TestUtils {
    private static final Logger LOGGER = Logger.getLogger(AccountsApi.class.getName());

    public static String getResposeFromFile(final String fileName) {
        File responseMessage;
        try {
            responseMessage = new ClassPathResource(fileName).getFile();
            return FileUtils.readFileToString(responseMessage, "utf-8");

        } catch (IOException e) {
            LOGGER.warning("Error when reading file " + e.getMessage());
        }
        return null;
    }
}
