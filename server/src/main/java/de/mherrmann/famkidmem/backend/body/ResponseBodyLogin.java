package de.mherrmann.famkidmem.backend.body;

public class ResponseBodyLogin extends ResponseBody {

    private String accessToken;
    private String passwordKeySalt;

    private ResponseBodyLogin(){}

    public ResponseBodyLogin(String accessToken, String passwordKeySalt){
        super("ok", "Login was successful");
        this.accessToken = accessToken;
        this.passwordKeySalt = passwordKeySalt;
    }

    public ResponseBodyLogin(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPasswordKeySalt() {
        return passwordKeySalt;
    }

    public void setPasswordKeySalt(String passwordKeySalt) {
        this.passwordKeySalt = passwordKeySalt;
    }
}
