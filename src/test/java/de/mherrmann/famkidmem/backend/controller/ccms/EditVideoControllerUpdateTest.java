package de.mherrmann.famkidmem.backend.controller.ccms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EditVideoControllerUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private EditVideoService editVideoService;

    @Before
    public void setup() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown() {
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
        testUtils.dropAll();
    }

    @Test
    public void shouldUpdateVideo() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        long countBefore = videoRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/edit/video/update")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateVideoRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully updated video: " + updateVideoRequest.getTitle());
        assertThat(videoRepository.existsByTitle(updateVideoRequest.getTitle())).isTrue();
        assertThat(videoRepository.count()).isEqualTo(countBefore);
    }

    @Test
    public void shouldFailUpdateVideoCausedByEntityNotFound() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setDesignator("invalid");

        shouldFailUpdateVideo(updateVideoRequest, "Entity does not exist. Type: Video; designator: invalid");
    }

    @Test
    public void shouldFailUpdateVideoCausedByMissingTitle() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setTitle("");

        shouldFailUpdateVideo(updateVideoRequest, "Title can not be empty.");
    }

    @Test
    public void shouldFailUpdateVideoCausedByMissingKey() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setKey("");

        shouldFailUpdateVideo(updateVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailUpdateVideoCausedByMissingIv() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setIv("");

        shouldFailUpdateVideo(updateVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailUpdateVideoCausedByMissingThumbnailKey() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setThumbnailKey("");

        shouldFailUpdateVideo(updateVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailUpdateVideoCausedByMissingThumbnailIv() throws Exception {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setThumbnailIv("");

        shouldFailUpdateVideo(updateVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailUpdateVideoCausedByEponymousVideoExists() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        addVideoRequest.setTitle("eponymous");
        updateVideoRequest.setTitle("eponymous");
        try {
            editVideoService.addVideo(addVideoRequest);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        shouldFailUpdateVideo(updateVideoRequest, "Video with same title already exists.");
    }


    private void shouldFailUpdateVideo(RequestBodyUpdateVideo updateVideoRequest, String expectedDetails) throws Exception {
        long countBefore = videoRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/edit/video/update")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateVideoRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(videoRepository.count()).isEqualTo(countBefore);
        assertThat(videoRepository.existsByTitle("title/")).isTrue();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBody jsonToResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
