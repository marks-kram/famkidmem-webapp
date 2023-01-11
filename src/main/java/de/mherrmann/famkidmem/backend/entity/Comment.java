package de.mherrmann.famkidmem.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Comment {
    @Id
    private String id;

    @Column(unique = true)
    private String cid;

    private String text;
    private long creation;
    private long modification;
    private boolean modified;
    private boolean removed;

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
        this.cid = UUID.randomUUID().toString();
        this.text = text;
        this.creation = System.currentTimeMillis();
        this.modification = -1L;
        this.user = user;
        this.video = video;
        this.key = key;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public String getCid() {
        return cid;
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

    public boolean isRemoved() {
        return removed;
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

    public void setRemovedTrue() {
        this.removed = true;
    }
}
