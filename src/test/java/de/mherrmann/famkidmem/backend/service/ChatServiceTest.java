package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.AddMessageRequest;
import de.mherrmann.famkidmem.backend.entity.ChatMessage;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.ChatMessageRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private TestUtils testUtils;

    private UserEntity user;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Before
    public void setup() throws Exception {
        testUtils.dropAll();
        user = testUtils.createTestUser("loginHash", false);
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddMessage() {
        AddMessageRequest addMessageRequest = createAddMessageRequest();

        chatService.addMessage(addMessageRequest, user);

        assertThat(chatMessageRepository.count()).isEqualTo(1);
        assertThat(chatMessageRepository.findAll().iterator()).hasNext();
        ChatMessage message = chatMessageRepository.findAll().iterator().next();
        assertThat(message.getMessage()).isEqualTo(addMessageRequest.getMessage());
        assertThat(message.getUser().getUsername()).isEqualTo(user.getUsername());
    }

   @Test
    public void shouldGet2Messages() {
        AddMessageRequest addMessageRequest1 = createAddMessageRequest();
        AddMessageRequest addMessageRequest2 = createAddMessageRequest();
        addMessageRequest1.setMessage("text1");
        addMessageRequest2.setMessage("text2");
        chatService.addMessage(addMessageRequest1, user);
        chatService.addMessage(addMessageRequest2, user);

        List<ChatMessage> messages = chatService.getAllMessages();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getMessage()).isEqualTo(addMessageRequest1.getMessage());
        assertThat(messages.get(1).getMessage()).isEqualTo(addMessageRequest2.getMessage());
    }

    @Test
    public void shouldGetTheNewMessage() {
        AddMessageRequest addMessageRequest = createAddMessageRequest();
        chatService.addMessage(addMessageRequest, user);

        List<ChatMessage> messages1 = chatService.getMessagesSince(System.currentTimeMillis() - 2000);
        List<ChatMessage> messages2 = chatService.getMessagesSince(System.currentTimeMillis() + 2000);

        assertThat(messages1).hasSize(1);
        assertThat(messages2).isEmpty();
        assertThat(messages1.get(0).getMessage()).isEqualTo(addMessageRequest.getMessage());
    }

    /* @Test
    public void shouldGetEmptyCommentsList() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setTitle("another");
        editVideoService.addVideo(addVideoRequest);
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        addCommentRequest.setText("text1");
        addCommentRequest.setVideoTitle("another");
        chatService.addComment(addCommentRequest, user);

        List<Comment> comments = chatService.getComments(video.getTitle());

        assertThat(comments).isEmpty();
    }

    @Test
    public void shouldUpdateComment() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        chatService.addComment(addCommentRequest, user);

        chatService.updateComment(updateCommentRequest, user);

        assertThat(chatMessageRepository.count()).isEqualTo(1);
        assertThat(chatMessageRepository.findAll().iterator()).hasNext();
        Comment comment = chatMessageRepository.findAll().iterator().next();
        assertThat(comment.getText()).isEqualTo(updateCommentRequest.getText());
        assertThat(comment.isModified()).isTrue();
    }

    @Test
    public void shouldDeleteComment() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        AddCommentRequest addCommentRequest1 = createAddMessageRequest();
        AddCommentRequest addCommentRequest2 = createAddMessageRequest();
        addCommentRequest2.setText("other");
        chatService.addComment(addCommentRequest1, user);
        chatService.addComment(addCommentRequest2, user);

        chatService.removeComment(removeCommentRequest, user);
        assertThat(chatMessageRepository.count()).isEqualTo(1);
        assertThat(chatMessageRepository.findAll().iterator()).hasNext();
        Comment comment = chatMessageRepository.findAll().iterator().next();
        assertThat(comment.getText()).isEqualTo(addCommentRequest2.getText());
    }


    @Test
    public void shouldThrowExceptionDueToMissingVideoOnAddComment() {
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        addCommentRequest.setVideoTitle("missing");
        Exception exception = null;

        try {
            chatService.addComment(addCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, addCommentRequest.getVideoTitle()).getMessage()
        );
        assertThat(chatMessageRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldThrowExceptionDueToMissingVideoOnGetComments() throws Exception {
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        chatService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            chatService.getComments("missing");
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, "missing").getMessage()
        );
        assertThat(chatMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingVideoOnUpdateComment() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        updateCommentRequest.setVideoTitle("missing");
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        chatService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            chatService.updateComment(updateCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, updateCommentRequest.getVideoTitle()).getMessage()
        );
        assertThat(chatMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingCommentOnUpdateComment() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        updateCommentRequest.setOldText("missing");
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        chatService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            chatService.updateComment(updateCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Comment.class, updateCommentRequest.getOldText()).getMessage()
        );
        assertThat(chatMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingVideoOnRemoveComment() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        removeCommentRequest.setVideoTitle("missing");
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        chatService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            chatService.removeComment(removeCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, removeCommentRequest.getVideoTitle()).getMessage()
        );
        assertThat(chatMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingCommentOnRemoveComment() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        removeCommentRequest.setText("missing");
        AddCommentRequest addCommentRequest = createAddMessageRequest();
        chatService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            chatService.removeComment(removeCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Comment.class, removeCommentRequest.getText()).getMessage()
        );
        assertThat(chatMessageRepository.count()).isEqualTo(1);
    } */

    private AddMessageRequest createAddMessageRequest() {
        AddMessageRequest addMessageRequest = new AddMessageRequest();
        addMessageRequest.setMessage("test");
        addMessageRequest.setKey("key");
        addMessageRequest.setIv("iv");
        return addMessageRequest;
    }

    /*private UpdateCommentRequest createUpdateCommentRequest () {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest();
        updateCommentRequest.setText("updated");
        updateCommentRequest.setOldText("test");
        updateCommentRequest.setVideoTitle(video.getTitle());
        return updateCommentRequest;
    }

    private RemoveCommentRequest createRemoveCommentRequest () {
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest();
        removeCommentRequest.setText("test");
        removeCommentRequest.setVideoTitle(video.getTitle());
        return removeCommentRequest;
    }*/


}
