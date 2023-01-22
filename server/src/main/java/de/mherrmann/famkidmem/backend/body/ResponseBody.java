package de.mherrmann.famkidmem.backend.body;

public class ResponseBody {

    private String message;
    private String details;
    private String exception;

    protected ResponseBody(){}

    public ResponseBody(String message, String details, Exception exception) {
        this.message = message;
        this.details = details;
        this.exception = exception.getClass().getSimpleName();
    }

    public ResponseBody(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
