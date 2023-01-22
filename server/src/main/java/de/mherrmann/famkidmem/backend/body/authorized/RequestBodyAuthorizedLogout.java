package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedLogout extends RequestBodyAuthorized {
    private boolean global;

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }
}
