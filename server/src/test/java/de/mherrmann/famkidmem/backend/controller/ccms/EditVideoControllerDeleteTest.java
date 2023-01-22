package de.mherrmann.famkidmem.backend.controller.ccms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EditVideoControllerDeleteTest {

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
        editVideoService.addVideo(testUtils.createAddAnotherVideoRequest());
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown() {
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
        testUtils.dropAll();
    }

    @Test
    public void shouldDeleteVideo() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(delete("/ccms/edit/video/delete/{designator}", "title_")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully removed video: title/");
        assertThat(videoRepository.existsByTitle("title/")).isFalse();
        assertThat(videoRepository.existsByTitle("video2")).isTrue();
    }

    @Test
    public void shouldFailDeleteVideoCausedByEntityNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete("/ccms/edit/video/delete/{designator}", "invalid")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("Entity does not exist. Type: Video; designator: invalid");
        assertThat(videoRepository.existsByTitle("title/")).isTrue();
        assertThat(videoRepository.existsByTitle("video2")).isTrue();
    }

    private static ResponseBody jsonToResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
