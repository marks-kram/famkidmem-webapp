package de.mherrmann.famkidmem.backend.body.content;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.entity.ChatMessage;

import java.util.List;

public class ResponseBodyMessages extends ResponseBody {

    private List<ChatMessage> messages;
    private String userKey;

    @SuppressWarnings("unused") // used reflective
    private ResponseBodyMessages(){}

    public ResponseBodyMessages(List<ChatMessage> messages, String userKey) {
        super("success", "");
        this.messages = messages;
        this.userKey = userKey;
    }

    public ResponseBodyMessages(Exception ex) {
        super("error", "could not get chat messages", ex);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getUserKey() {
        return userKey;
    }
}
