package de.mherrmann.famkidmem.backend.body;

public class RemoveCommentRequest {
    private String text;
    private String videoTitle;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
}
