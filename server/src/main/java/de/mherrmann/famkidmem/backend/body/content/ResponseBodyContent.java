package de.mherrmann.famkidmem.backend.body.content;

import de.mherrmann.famkidmem.backend.body.ResponseBody;

public abstract class ResponseBodyContent extends ResponseBody {

    private String masterKey;

    protected ResponseBodyContent(){}

    public ResponseBodyContent(String masterKey){
        super("ok", "Successfully got content.");
        this.masterKey = masterKey;
    }

    public ResponseBodyContent(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}
