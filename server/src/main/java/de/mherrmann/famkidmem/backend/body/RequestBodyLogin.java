package de.mherrmann.famkidmem.backend.body;


public class RequestBodyLogin {

    private String username;
    private String loginHash;
    private boolean permanent;

    public RequestBodyLogin(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginHash() {
        return loginHash;
    }

    public void setLoginHash(String loginHash) {
        this.loginHash = loginHash;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }
}
