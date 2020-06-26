package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoTsControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUtils testUtils;

    private UserEntity testUser;
    private ResponseBodyLogin testLogin;

    private static final String LOGIN_HASH = "loginHash";

    @Before
    public void setup() throws Exception {
        createTestUser();
        testLogin  = userService.login(testUser.getUsername(), LOGIN_HASH);
        testUtils.createTestFile("sequence.ts");
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteAuthTokenHashFile();
    }

    @Test
    public void shouldGetTsFileAuthorizedByLogin() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/ts/{accessToken}/{filename}", testLogin.getAccessToken(), "sequence.ts"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("sequence.ts");
        assertThat(mvcResult.getResponse().getHeaders("Content-Length").get(0)).isEqualTo("11");
        assertThat(mvcResult.getResponse().getHeaders("Content-Type").get(0)).isEqualTo("video/vnd.dlna.mpeg-tts");
    }

    @Test
    public void shouldFailGetTsFileCausedByInvalidLogin() throws Exception {
        shouldFailGetTsFile("invalid", "sequence.ts", "You are not allowed to do this: get ts file");
    }

    @Test
    public void shouldFailGetTsFileCausedByFileNotFound() throws Exception {
        shouldFailGetTsFile(testLogin.getAccessToken(), "invalid.ts", "File does not exist or is not a file. filename: invalid.ts");
    }

    @Test
    public void shouldGetTsFileAuthorizedByApiKey() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/ts/{filename}", "sequence.ts")
                .header("CCMS_AUTH_TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("sequence.ts");
        assertThat(mvcResult.getResponse().getHeaders("Content-Length").get(0)).isEqualTo("11");
        assertThat(mvcResult.getResponse().getHeaders("Content-Type").get(0)).isEqualTo("video/vnd.dlna.mpeg-tts");
    }

    @Test
    public void shouldFailGetTsFileAuthorizedByApiKeyCausedByFileNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/ts/{filename}", "invalid.ts")
                .header("CCMS_AUTH_TOKEN", "token"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        ResponseBody body = jsonToLoginResponse(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("File does not exist or is not a file. filename: invalid.ts");
    }

    private void shouldFailGetTsFile(String accessToken, String filename, String expectedDetails) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/ts/{accessToken}/{filename}", accessToken, filename))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        ResponseBody body = jsonToLoginResponse(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo(expectedDetails);
    }


    private void createTestUser() {
        testUser = testUtils.createTestUser(LOGIN_HASH);
    }

    private static ResponseBody jsonToLoginResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
