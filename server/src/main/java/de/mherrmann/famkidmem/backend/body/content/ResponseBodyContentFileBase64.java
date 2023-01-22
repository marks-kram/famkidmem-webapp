package de.mherrmann.famkidmem.backend.body.content;

public class ResponseBodyContentFileBase64 extends ResponseBodyContent {

    private String base64;

    private ResponseBodyContentFileBase64(){}

    public ResponseBodyContentFileBase64(String masterKey, String base64) {
        super(masterKey);
        this.base64 = base64;
    }

    public ResponseBodyContentFileBase64(Exception ex) {
        super(ex);
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
