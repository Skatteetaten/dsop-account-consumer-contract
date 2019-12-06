package dsop.konsument.kontrakt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ske.ekstkom.utsending.kontoopplysninger.interfaces.ekstern.Error;

@RestControllerAdvice(annotations= RestController.class)
public class ExceptionTranslator {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Error> processIllegalArgumentException(IllegalArgumentException e) throws Exception {
        Error error = new Error();
        error.setCode("ACC-001");
        error.setMessage("Bad request. Ugyldige parametere i forespørselen");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
