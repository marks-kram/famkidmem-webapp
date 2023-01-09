package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.AddCommentRequest;
import de.mherrmann.famkidmem.backend.body.RemoveCommentRequest;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.UpdateCommentRequest;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyComments;
import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.CommentRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.service.CommentService;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TestUtils testUtils;

    private UserEntity user;
    private Video video;

    private String accessToken;
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
        accessToken = testUtils.createUserSession(user.getUsername(), "loginHash");
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddComment() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/comment/add/{accessToken}", accessToken)
                        .contentType("application/json")
                        .content(asJsonString(createAddCommentRequest()))
                ).andExpect(status().isCreated()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("success");
        assertThat(body.getDetails()).isEqualTo("comment added");
        assertThat(body.getException()).isNull();
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldGetComments() throws Exception {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/comment/get/{videoTitle}/{accessToken}", video.getTitle(), accessToken))
                .andExpect(status().isOk())
                .andReturn();

        ResponseBodyComments body = jsonToResponseBodyComments(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("success");
        assertThat(body.getDetails()).isEqualTo("");
        assertThat(body.getComments()).hasSize(1);
        assertThat(body.getComments().get(0).getText()).isEqualTo(addCommentRequest.getText());
        assertThat(body.getException()).isNull();
    }

    @Test
    public void shouldUpdateComment() throws Exception {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        commentService.addComment(addCommentRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(put("/api/comment/update/{accessToken}", accessToken)
                .contentType("application/json")
                .content(asJsonString(updateCommentRequest))
        ).andExpect(status().isOk()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("success");
        assertThat(body.getDetails()).isEqualTo("comment updated");
        assertThat(body.getException()).isNull();
        assertThat(commentRepository.count()).isEqualTo(1);
        assertThat(commentRepository.findAll().iterator()).hasNext();
        Comment comment = commentRepository.findAll().iterator().next();
        assertThat(comment.getText()).isEqualTo(updateCommentRequest.getText());
    }

    @Test
    public void shouldDeleteComment() throws Exception {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        commentService.addComment(addCommentRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(delete("/api/comment/delete/{accessToken}", accessToken)
                .contentType("application/json")
                .content(asJsonString(removeCommentRequest))
        ).andExpect(status().isOk()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("success");
        assertThat(body.getDetails()).isEqualTo("comment removed");
        assertThat(body.getException()).isNull();
        assertThat(commentRepository.findAll().iterator()).hasNext();
        assertThat(commentRepository.findAll().iterator().next().isRemoved()).isTrue();
    }


    @Test
    public void shouldFailToAddCommentDueToMissingVideo() throws Exception {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        addCommentRequest.setVideoTitle("missing");

        MvcResult mvcResult = this.mockMvc.perform(post("/api/comment/add/{accessToken}", accessToken)
                .contentType("application/json")
                .content(asJsonString(addCommentRequest))
        ).andExpect(status().isBadRequest()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("comment not added");
        assertThat(body.getException()).isEqualTo(EntityNotFoundException.class.getSimpleName());
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFailToGetCommentsDueToMissingVideo() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/comment/get/{videoTitle}/{accessToken}", "missing", accessToken))
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseBodyComments body = jsonToResponseBodyComments(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("could not get comments");
        assertThat(body.getComments()).isNull();
        assertThat(body.getException()).isEqualTo(EntityNotFoundException.class.getSimpleName());
    }

    @Test
    public void shouldFailToUpdateCommentDueToMissingVideo() throws Exception {
        UpdateCommentRequest updateCommentRequest = createUpdateCommentRequest();
        updateCommentRequest.setVideoTitle("missing");

        MvcResult mvcResult = this.mockMvc.perform(put("/api/comment/update/{accessToken}", accessToken)
                .contentType("application/json")
                .content(asJsonString(updateCommentRequest))
        ).andExpect(status().isBadRequest()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("comment not updated");
        assertThat(body.getException()).isEqualTo(EntityNotFoundException.class.getSimpleName());
    }

    @Test
    public void shouldFailToDeleteCommentDueToMissingVideo() throws Exception {
        RemoveCommentRequest removeCommentRequest = createRemoveCommentRequest();
        removeCommentRequest.setVideoTitle("missing");
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(delete("/api/comment/delete/{accessToken}", accessToken)
                .contentType("application/json")
                .content(asJsonString(removeCommentRequest))
        ).andExpect(status().isBadRequest()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("comment not removed");
        assertThat(body.getException()).isEqualTo(EntityNotFoundException.class.getSimpleName());
        assertThat(commentRepository.findAll().iterator()).hasNext();
        assertThat(commentRepository.findAll().iterator().next().isRemoved()).isFalse();
    }

    @Test
    public void shouldFailToAddCommentDueToInvalidAccessToken() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/comment/add/{accessToken}", "invalid")
                        .contentType("application/json")
                        .content(asJsonString(createAddCommentRequest()))
                ).andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("comment not added");
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
    }

    @Test
    public void shouldFailToGetCommentsDueToInvalidAccessToken() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/comment/get/{videoTitle}/{accessToken}", "title", "invalid")
                ).andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ResponseBodyComments body = jsonToResponseBodyComments(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("could not get comments");
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
    }

    @Test
    public void shouldFailToUpdateCommentDueToInvalidAccessToken() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(put("/api/comment/update/{accessToken}", "invalid")
                .contentType("application/json")
                .content(asJsonString(createUpdateCommentRequest()))
        ).andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("comment not updated");
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
    }

    @Test
    public void shouldFailToDeleteCommentDueToInvalidAccessToken() throws Exception {
        AddCommentRequest addCommentRequest = createAddCommentRequest();
        commentService.addComment(addCommentRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(delete("/api/comment/delete/{accessToken}", "invalid")
                .contentType("application/json")
                .content(asJsonString(createRemoveCommentRequest()))
        ).andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("comment not removed");
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
        assertThat(commentRepository.findAll().iterator()).hasNext();
        assertThat(commentRepository.findAll().iterator().next().isRemoved()).isFalse();
    }

    private AddCommentRequest createAddCommentRequest () {
        AddCommentRequest addCommentRequest = new AddCommentRequest();
        addCommentRequest.setText("test");
        addCommentRequest.setKey("key");
        addCommentRequest.setIv("iv");
        addCommentRequest.setVideoTitle("title");
        return addCommentRequest;
    }

    private UpdateCommentRequest createUpdateCommentRequest () {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest();
        updateCommentRequest.setText("updated");
        updateCommentRequest.setOldText("test");
        updateCommentRequest.setVideoTitle("title");
        return updateCommentRequest;
    }

    private RemoveCommentRequest createRemoveCommentRequest () {
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest();
        removeCommentRequest.setText("test");
        removeCommentRequest.setVideoTitle("title");
        return removeCommentRequest;
    }

    private static ResponseBody jsonToResponseBody(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBodyComments jsonToResponseBodyComments(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyComments.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
