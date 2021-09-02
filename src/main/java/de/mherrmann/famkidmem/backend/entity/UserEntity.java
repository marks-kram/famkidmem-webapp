package de.mherrmann.famkidmem.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    private String id;

    private String username;
    private String displayName;
    private String passwordKeySalt;
    private String loginHashHash;
    private String masterKey;

    private boolean permission2;

    private boolean init; //indicates the user has to change username and password after login
    private boolean reset; //indicates the user has to change password after login

    protected UserEntity(){}

    public UserEntity(String username, String displayName, String passwordKeySalt, String loginHashHash,
                      String masterKey, boolean permission2) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.displayName = displayName;
        this.passwordKeySalt = passwordKeySalt;
        this.loginHashHash = loginHashHash;
        this.masterKey = masterKey;
        this.permission2 = permission2;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public String getPasswordKeySalt() {
        return passwordKeySalt;
    }

    public void setPasswordKeySalt(String passwordKeySalt) {
        this.passwordKeySalt = passwordKeySalt;
    }

    @JsonIgnore
    public String getLoginHashHash() {
        return loginHashHash;
    }

    public void setLoginHashHash(String loginHashHash) {
        this.loginHashHash = loginHashHash;
    }

    @JsonIgnore
    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public boolean isPermission2() {
        return permission2;
    }

    public void setPermission2(boolean permission2) {
        this.permission2 = permission2;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }
}
