package de.mherrmann.famkidmem.backend.controller.ccms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.backend.entity.Video;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EditVideoControllerGetTest {

    private static final String THUMBNAIL_BASE64 = "dGh1bWJuYWls";
    private static final String M3U8_BASE64 = "bTN1OA==";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private EditVideoService editVideoService;

    @Before
    public void setUp() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        testUtils.createAuthTokenHashFile();
        testUtils.createTestFile("thumbnail");
        testUtils.createTestFile("m3u8");
    }

    @After
    public void teardown() {
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
        testUtils.dropAll();
    }

    @Test
    public void shouldGetVideos() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/get")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        List<Video> videos = jsonToResponse(mvcResult.getResponse().getContentAsString()).getVideos();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully got videos");
        assertThat(videos.size()).isEqualTo(1);
        assertThat(videos.get(0).getTitle()).isEqualTo("title/");
    }

    @Test
    public void shouldGetSingleVideo() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/get/title_")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        List<Video> videos = jsonToResponse(mvcResult.getResponse().getContentAsString()).getVideos();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully got video");
        assertThat(videos.size()).isEqualTo(1);
        assertThat(videos.get(0).getTitle()).isEqualTo("title/");
    }

    @Test
    public void shouldGetThumbnail() throws Exception {
        shouldGetFileBase64("thumbnail", THUMBNAIL_BASE64);
    }

    @Test
    public void shouldGetM3u8() throws Exception {
        shouldGetFileBase64("m3u8", M3U8_BASE64);
    }

    @Test
    public void shouldGetFileBase64CausedByFileNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/base64/{filename}", "invalid")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        ResponseBodyContentFileBase64 base64 = jsonToResponseContentFileBase64(mvcResult.getResponse().getContentAsString());
        assertThat(base64.getMessage()).isEqualTo("error");
        assertThat(base64.getDetails()).isEqualTo("File does not exist or is not a file. filename: invalid");
        assertThat(base64.getMasterKey()).isNull();
        assertThat(base64.getBase64()).isNull();
    }

    private void shouldGetFileBase64(String filename, String base64String) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/base64/{filename}", filename)
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        ResponseBodyContentFileBase64 base64 = jsonToResponseContentFileBase64(mvcResult.getResponse().getContentAsString());
        assertThat(base64.getMessage()).isEqualTo("ok");
        assertThat(base64.getDetails()).isEqualTo("Successfully got content.");
        assertThat(base64.getBase64()).isEqualTo(base64String);
    }

    private static ResponseBodyContentFileBase64 jsonToResponseContentFileBase64(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyContentFileBase64.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBodyGetVideos jsonToResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyGetVideos.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
