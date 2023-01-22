package de.mherrmann.famkidmem.backend.controller.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileDeleteControllerTest {

    private static final String TEST_NAME = "test.txt";
    private static final String TEST_DIRECTORY = "./files/";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setUp() throws IOException {
        testUtils.createAuthTokenHashFile();
        testUtils.createTestFile(TEST_NAME);
    }

    @After
    public void tearDown(){
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
    }

    @Test
    public void shouldDeleteFile() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete("/ccms/delete/{fileName}", TEST_NAME)
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(new File(TEST_DIRECTORY + TEST_NAME).exists()).isFalse();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("ok");
    }

    @Test
    public void shouldFailDeleteFile() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete("/ccms/delete/{fileName}", "invalid")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(new File(TEST_DIRECTORY + TEST_NAME).exists()).isTrue();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("error: Can not delete non-existing file.");
    }



}
