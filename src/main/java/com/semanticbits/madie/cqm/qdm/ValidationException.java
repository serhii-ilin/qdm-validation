package com.semanticbits.madie.cqm.qdm;

import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        this.errors = Collections.unmodifiableList(errors);
    }

    public List getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "errors=" + errors +
                '}';
    }
}
