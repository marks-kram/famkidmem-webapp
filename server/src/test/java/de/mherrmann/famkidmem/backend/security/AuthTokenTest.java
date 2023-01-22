package de.mherrmann.famkidmem.backend.security;

import de.mherrmann.famkidmem.backend.TestUtils;
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

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AuthTokenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setup() throws IOException {
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown() {
        testUtils.deleteAuthTokenHashFile();
    }


    @Test
    public void shouldBeStatusOk() throws Exception {
        this.mockMvc.perform(get("/ccms/admin/user/get")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldBeStatusForbiddenCausedByInvalidToken() throws Exception {
        this.mockMvc.perform(get("/ccms/admin/user/get")
                .header("CCMS-AUTH-TOKEN", "invalid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void shouldBeStatusForbiddenCausedByEmptyToken() throws Exception {
        this.mockMvc.perform(get("/ccms/admin/user/get")
                .header("CCMS-AUTH-TOKEN", ""))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void shouldBeStatusForbiddenCausedByMissingToken() throws Exception {
        this.mockMvc.perform(get("/ccms/admin/user/get"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

}
