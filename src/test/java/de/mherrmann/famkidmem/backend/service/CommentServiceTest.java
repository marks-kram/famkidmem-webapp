package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.AddCommentRequest;
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

    private AddCommentRequest createAddCommentRequest () {
        AddCommentRequest addCommentRequest = new AddCommentRequest();
        addCommentRequest.setText("test");
        addCommentRequest.setKey("key");
        addCommentRequest.setIv("iv");
        addCommentRequest.setVideoTitle(video.getTitle());
        return addCommentRequest;
    }

}
