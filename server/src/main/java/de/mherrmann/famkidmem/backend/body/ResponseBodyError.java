package de.mherrmann.famkidmem.backend.body;

public class ResponseBodyError extends ResponseBody {
    private ResponseBodyError(){}

    public ResponseBodyError(Exception exception){
        super("error", exception.getMessage(), exception);
    }

    public ResponseBodyError(String error){
        super("error", "failure: " + error);
    }
}
