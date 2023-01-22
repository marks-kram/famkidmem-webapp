package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedChangePassword extends RequestBodyAuthorized {

    private String newLoginHash;
    private String newPasswordKeySalt;
    private String newMasterKey;

    public String getNewLoginHash() {
        return newLoginHash;
    }

    public void setNewLoginHash(String newLoginHash) {
        this.newLoginHash = newLoginHash;
    }

    public String getNewPasswordKeySalt() {
        return newPasswordKeySalt;
    }

    public void setNewPasswordKeySalt(String newPasswordKeySalt) {
        this.newPasswordKeySalt = newPasswordKeySalt;
    }

    public String getNewMasterKey() {
        return newMasterKey;
    }

    public void setNewMasterKey(String newMasterKey) {
        this.newMasterKey = newMasterKey;
    }
}
