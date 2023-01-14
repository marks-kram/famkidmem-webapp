package de.mherrmann.famkidmem.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class ChatMessage {

    @Id
    private String id;

    private String message;
    private long timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "key_id", referencedColumnName = "id")
    private Key key;

    protected ChatMessage(){}

    public ChatMessage (String message, UserEntity user, Key key) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.user = user;
        this.key = key;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UserEntity getUser() {
        return user;
    }

    public Key getKey() {
        return key;
    }
}
