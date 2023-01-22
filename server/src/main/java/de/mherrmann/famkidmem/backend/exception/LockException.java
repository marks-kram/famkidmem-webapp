package de.mherrmann.famkidmem.backend.exception;

public class LockException extends RuntimeException {

    public LockException(){
        super("This user is locked for login. Too much invalid login attempts");
    }

}
