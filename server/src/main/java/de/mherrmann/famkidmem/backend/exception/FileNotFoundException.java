package de.mherrmann.famkidmem.backend.exception;

public class FileNotFoundException extends Exception {
    public FileNotFoundException(String filename){
        super(String.format("File does not exist or is not a file. filename: %s", filename));
    }
}
