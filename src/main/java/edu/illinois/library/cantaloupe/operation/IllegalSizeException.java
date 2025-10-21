package edu.illinois.library.cantaloupe.operation;

import edu.illinois.library.cantaloupe.config.Key;
import edu.illinois.library.cantaloupe.http.Status;

public class IllegalSizeException extends ValidationException {

    IllegalSizeException() {
        super(Status.FORBIDDEN, "The requested pixel area exceeds the maximum threshold (" +
                Key.MAX_PIXELS + ") set in the configuration.");
    }

}
