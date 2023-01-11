package de.mherrmann.famkidmem.backend.body;

public class RemoveCommentRequest {
    private String cid;
    private String videoTitle;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
}
