package de.mherrmann.famkidmem.backend.body;

public class UpdateCommentRequest {
    private String text;
    private String cid;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
