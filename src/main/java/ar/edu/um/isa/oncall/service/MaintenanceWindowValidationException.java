package ar.edu.um.isa.oncall.service;

import java.io.Serial;

public class MaintenanceWindowValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final String errorKey;

    public MaintenanceWindowValidationException(String message, String entityName, String errorKey) {
        super(message);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
