package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.*;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyMessages;
import de.mherrmann.famkidmem.backend.entity.ChatMessage;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.service.ChatService;
import de.mherrmann.famkidmem.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping(value = "/add/{accessToken}")
    public ResponseEntity<ResponseBody> addMessage(@RequestBody AddMessageRequest addMessageRequest, @PathVariable String accessToken) {
        try {
            UserEntity user = userService.getUser(accessToken, "add Comment");
            chatService.addMessage(addMessageRequest, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseBody("success", "chat message added"));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", "chat message not added", exception));
        }
    }

    @GetMapping(value = "/get-all/{accessToken}")
    public ResponseEntity<ResponseBodyMessages> getAllMessages(@PathVariable String accessToken) {
        try {
            userService.getUser(accessToken, "get comments");
            List<ChatMessage> messages = chatService.getAllMessages();
            return ResponseEntity.ok(new ResponseBodyMessages(messages));
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new ResponseBodyMessages(ex));
        }
    }

    @GetMapping(value = "/get-since/{threshold}/{accessToken}")
    public ResponseEntity<ResponseBodyMessages> getAllMessages(@PathVariable long threshold, @PathVariable String accessToken) {
        try {
            userService.getUser(accessToken, "get comments");
            List<ChatMessage> messages = chatService.getMessagesSince(threshold);
            return ResponseEntity.ok(new ResponseBodyMessages(messages));
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new ResponseBodyMessages(ex));
        }
    }
}
