package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedChangeUsername extends RequestBodyAuthorized {

    private String newUsername;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
