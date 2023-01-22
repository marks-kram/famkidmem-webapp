package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedChangeUsername extends RequestBodyAuthorized {

    private String newUsername;
    private String newLoginHash;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewLoginHash() {
        return newLoginHash;
    }

    public void setNewLoginHash(String newLoginHash) {
        this.newLoginHash = newLoginHash;
    }
}
