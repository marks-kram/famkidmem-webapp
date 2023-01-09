package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.AddCommentRequest;
import de.mherrmann.famkidmem.backend.body.RemoveCommentRequest;
import de.mherrmann.famkidmem.backend.body.UpdateCommentRequest;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.CommentRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private TestUtils testUtils;

    private UserEntity user;
    private Video video;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Before
    public void setup() throws Exception {
        testUtils.dropAll();
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        Optional<Video> videoOptional = videoRepository.findByTitle(testUtils.createAddVideoRequest().getTitle());
        //noinspection OptionalGetWithoutIsPresent
        video = videoOptional.get();
        user = testUtils.createTestUser("loginHash", false);
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddComment() throws EntityNotFoundException {
        AddCommentRequest addCommentRequest = createAddCommentRequest();

        commentService.addComment(addCommentRequest, user);

        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findAll().iterator()).hasNext();
        Comment comment = commentRepository.findAll().iterator().next();
        assertThat(comment.getText()).isEqualTo(addCommentRequest.getText());
        assertThat(comment.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(comment.getVideo().getTitle()).isEqualTo(video.getTitle());
        assertThat(comment.isRemoved()).isFalse();
    }

    @Test
    public void shouldGet2Comments() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setTitle("another");
        editVideoService.addVideo(addVideoRequest);
        AddCommentRequest addCommentRequest1 = createAddCommentRequest();
        AddCommentRequest addCommentRequest2 = createAddCommentRequest();
        AddCommentRequest addCommentRequest3 = createAddCommentRequest();
        addCommentRequest1.setText("text1");
        addCommentRequest2.setText("text2");
        addCommentRequest3.setText("text3");
        addCommentRequest3.setVideoTitle("another");
        commentService.addComment(addCommentRequest1, user);
        commentService.addComment(addCommentRequest2, user);
        commentService.addComment(addCommentRequest3, user);

        List<Comment> comments = commentService.getComments(video.getTitle());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getText()).isEqualTo("text1");
        assertThat(comments.get(1).getText()).isEqualTo("text2");
    }

    @Test
    public void shouldGetEmptyCommentsList() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setTitle("another");
        editVideoService.addVideo(addVideoRequest);
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        addCommentRequest.setText("text1");
        addCommentRequest.setVideoTitle("another");
        commentService.addComment(addCommentRequest, user);

        List<Comment> comments = commentService.getComments(video.getTitle());

        assertThat(comments).isEmpty();
    }

    @Test
    public void shouldUpdateComment() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);

        commentService.updateComment(updateCommentRequest, user);

        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findAll().iterator()).hasNext();
        Comment comment = commentRepository.findAll().iterator().next();
        assertThat(comment.getText()).isEqualTo(updateCommentRequest.getText());
        assertThat(comment.isModified()).isTrue();
        assertThat(comment.isRemoved()).isFalse();
    }

    @Test
    public void shouldDeleteComment() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);

        commentService.removeComment(removeCommentRequest, user);
        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findAll().iterator()).hasNext();
        Comment comment = commentRepository.findAll().iterator().next();
        assertThat(comment.getText()).isNull();
        assertThat(comment.isRemoved()).isTrue();
    }


    @Test
    public void shouldThrowExceptionDueToMissingVideoOnAddComment() {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        addCommentRequest.setVideoTitle("missing");
        Exception exception = null;

        try {
            commentService.addComment(addCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, addCommentRequest.getVideoTitle()).getMessage()
        );
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldThrowExceptionDueToMissingVideoOnGetComments() throws Exception {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            commentService.getComments("missing");
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, "missing").getMessage()
        );
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingVideoOnUpdateComment() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        updateCommentRequest.setVideoTitle("missing");
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            commentService.updateComment(updateCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, updateCommentRequest.getVideoTitle()).getMessage()
        );
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingCommentOnUpdateComment() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        updateCommentRequest.setOldText("missing");
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            commentService.updateComment(updateCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Comment.class, updateCommentRequest.getOldText()).getMessage()
        );
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionDueToMissingVideoOnRemoveComment() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        removeCommentRequest.setVideoTitle("missing");
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            commentService.removeComment(removeCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Video.class, removeCommentRequest.getVideoTitle()).getMessage()
        );
        assertThat(commentRepository.findAll().iterator()).hasNext();
        assertThat(commentRepository.findAll().iterator().next().isRemoved()).isFalse();
    }

    @Test
    public void shouldThrowExceptionDueToMissingCommentOnRemoveComment() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        removeCommentRequest.setText("missing");
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);
        Exception exception = null;

        try {
            commentService.removeComment(removeCommentRequest, user);
        } catch (Exception ex) {
            exception = ex;
        }

        assertThat(exception).isNotNull().isInstanceOf(EntityNotFoundException.class).hasMessage(
                new EntityNotFoundException(Comment.class, removeCommentRequest.getText()).getMessage()
        );
        assertThat(commentRepository.findAll().iterator()).hasNext();
        assertThat(commentRepository.findAll().iterator().next().isRemoved()).isFalse();
    }

    private AddCommentRequest createAddCommentRequest () {
        AddCommentRequest addCommentRequest = new AddCommentRequest();
        addCommentRequest.setText("test");
        addCommentRequest.setKey("key");
        addCommentRequest.setIv("iv");
        addCommentRequest.setVideoTitle(video.getTitle());
        return addCommentRequest;
    }

    private UpdateCommentRequest createUpdateCommentRequest () {
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
    }


}
