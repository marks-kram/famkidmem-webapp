package de.mherrmann.famkidmem.backend.body.content;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.entity.Comment;

import java.util.List;

public class ResponseBodyComments extends ResponseBody {

    private List<Comment> comments;

    @SuppressWarnings("unused") // used reflective
    private ResponseBodyComments(){}

    public ResponseBodyComments(List<Comment> comments) {
        super("success", "");
        this.comments = comments;
    }

    public ResponseBodyComments(Exception ex) {
        super("error", "could not get comments", ex);
    }

    public List<Comment> getComments() {
        return comments;
    }
}
