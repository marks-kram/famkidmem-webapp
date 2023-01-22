package de.mherrmann.famkidmem.backend.exception;

public class LoginException extends RuntimeException {

    public LoginException(){
        super("Username or Password is wrong");
    }

}
