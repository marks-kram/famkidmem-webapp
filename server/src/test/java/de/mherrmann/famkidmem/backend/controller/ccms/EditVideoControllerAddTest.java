package de.mherrmann.famkidmem.backend.controller.ccms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
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

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EditVideoControllerAddTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private EditVideoService editVideoService;

    @Before
    public void setUp() throws IOException {
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown() {
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
        testUtils.dropAll();
    }

    @Test
    public void shouldAddVideo() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        long countBefore = videoRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/edit/video/add")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addVideoRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully added video: " + addVideoRequest.getTitle());
        assertThat(videoRepository.count()).isEqualTo(countBefore+1);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingTitle() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setTitle("");

        shouldFailAddVideo(addVideoRequest, "Title can not be empty.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingKey() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setKey("");

        shouldFailAddVideo(addVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingIv() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setIv("");

        shouldFailAddVideo(addVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingM3u8Key() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setM3u8Key("");

        shouldFailAddVideo(addVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingM3u8Iv() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setM3u8Iv("");

        shouldFailAddVideo(addVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailKey() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setThumbnailKey("");

        shouldFailAddVideo(addVideoRequest, "Key info missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailIv() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setThumbnailIv("");

        shouldFailAddVideo(addVideoRequest, "Key info missing.");
    }



    @Test
    public void shouldFailAddVideoCausedByMissingM3u8() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        new File("./files/m3u8").delete();

        shouldFailAddVideo(addVideoRequest, "File(s) missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnail() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        new File("./files/thumbnail").delete();

        shouldFailAddVideo(addVideoRequest, "File(s) missing.");
    }

    @Test
    public void shouldFailAddVideoCausedByEponymousVideoExists() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        try {
            editVideoService.addVideo(addVideoRequest);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        shouldFailAddVideo(addVideoRequest, "Video with same title already exists.");
    }


    private void shouldFailAddVideo(RequestBodyAddVideo addVideoRequest, String expectedDetails) throws Exception {
        long countBefore = videoRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/edit/video/add")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addVideoRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(videoRepository.count()).isEqualTo(countBefore);
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
