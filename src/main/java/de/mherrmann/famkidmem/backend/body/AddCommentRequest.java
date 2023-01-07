package de.mherrmann.famkidmem.backend.body;

import de.mherrmann.famkidmem.backend.entity.UserEntity;

public class AddCommentRequest {
    private String text;
    private String key;
    private String iv;
    private String videoId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
