package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.AddCommentRequest;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.UpdateCommentRequest;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyComments;
import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.service.CommentService;
import de.mherrmann.famkidmem.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping(value = "/add/{accessToken}")
    public ResponseEntity<ResponseBody> addComment(@RequestBody AddCommentRequest addCommentRequest, @PathVariable String accessToken) {
        try {
            UserEntity user = userService.getUser(accessToken, "add Comment");
            String cid = commentService.addComment(addCommentRequest, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseBody("success", "comment added: " + cid));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", "comment not added", exception));
        }
    }

    @GetMapping(value = "/get/{videoTitle}/{accessToken}")
    public ResponseEntity<ResponseBodyComments> getComments(@PathVariable String videoTitle, @PathVariable String accessToken) {
        try {
            userService.getUser(accessToken, "get comments");
            List<Comment> comments = commentService.getComments(videoTitle);
            return ResponseEntity.ok(new ResponseBodyComments(comments));
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new ResponseBodyComments(ex));
        }
    }

    @PutMapping(value = "/update/{accessToken}")
    public ResponseEntity<ResponseBody> updateComment(@RequestBody UpdateCommentRequest updateCommentRequest, @PathVariable String accessToken) {
        try {
            UserEntity user = userService.getUser(accessToken, "update comment");
            commentService.updateComment(updateCommentRequest, user);
            return ResponseEntity.ok().body(new ResponseBody("success", "comment updated"));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", "comment not updated", exception));
        }
    }

    @DeleteMapping(value = "/delete/{cid}/{accessToken}")
    public ResponseEntity<ResponseBody> deleteComment(@PathVariable String cid, @PathVariable String accessToken) {
        try {
            UserEntity user = userService.getUser(accessToken, "remove comment");
            commentService.removeComment(cid, user);
            return ResponseEntity.ok().body(new ResponseBody("success", "comment removed"));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", "comment not removed", exception));
        }
    }
}
