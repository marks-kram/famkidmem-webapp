package de.mherrmann.famkidmem.backend.body.authorized;

public abstract class RequestBodyAuthorized {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
