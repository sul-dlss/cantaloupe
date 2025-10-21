package edu.illinois.library.cantaloupe.operation;

import edu.illinois.library.cantaloupe.http.Status;

public class IllegalScaleException extends ValidationException {

    IllegalScaleException() {
        super(Status.FORBIDDEN, "Access denied for the requested scale.");
    }

}
