package de.mherrmann.famkidmem.backend.exception;

public class SecurityException extends RuntimeException {

    public SecurityException(String action){
        super("You are not allowed to do this: " + action);
    }

}
