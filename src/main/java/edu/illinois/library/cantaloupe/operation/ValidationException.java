package edu.illinois.library.cantaloupe.operation;

import edu.illinois.library.cantaloupe.http.Status;
import edu.illinois.library.cantaloupe.resource.ResourceException;

public class ValidationException extends ResourceException {

    public ValidationException(String message) {
        super(Status.BAD_REQUEST, message);
    }

    public ValidationException(Status status, String message) {
        super(status, message);
    }
}
