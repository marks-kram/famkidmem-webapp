package de.mherrmann.famkidmem.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Comment {
    @Id
    private String id;

    private String text;
    private long creation;
    private long modification;
    private boolean modified;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id")
    private Video video;

    @OneToOne
    @JoinColumn(name = "key_id", referencedColumnName = "id")
    private Key key;

    protected Comment(){}

    public Comment (String text, UserEntity user, Video video, Key key) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.creation = System.currentTimeMillis();
        this.modification = -1L;
        this.modified = false;
        this.user = user;
        this.video = video;
        this.key = key;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getCreation() {
        return creation;
    }

    public long getModification() {
        return modification;
    }

    public boolean isModified() {
        return modified;
    }

    public UserEntity getUser() {
        return user;
    }

    @JsonIgnore
    public Video getVideo() {
        return video;
    }

    public Key getKey() {
        return key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setModificationToNow() {
        this.modification = System.currentTimeMillis();
    }

    public void setModifiedTrue() {
        this.modified = true;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
