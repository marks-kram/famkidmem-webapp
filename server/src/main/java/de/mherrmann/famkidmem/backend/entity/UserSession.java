package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class UserSession {

    @Id
    private String id;

    private String accessToken;
    private boolean permanent;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    private Timestamp created;

    protected UserSession(){}

    public UserSession(UserEntity user, String accessToken, boolean permanent){
        this.id = UUID.randomUUID().toString();
        this.setUserEntity(user);
        this.setAccessToken(accessToken);
        this.setCreated(new Timestamp(System.currentTimeMillis()));
        this.permanent = permanent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }
}
