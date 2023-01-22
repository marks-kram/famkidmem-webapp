package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.exception.FileDeleteException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileDeleteServiceTest {

    private static final String TEST_NAME = "test.txt";
    private static final String TEST_DIRECTORY = Application.filesDir;

    @Autowired
    private FileDeleteService fileDeleteService;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setUp() throws IOException{
        testUtils.createTestFile(TEST_NAME);
    }

    @After
    public void tearDown(){
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldDeleteFile() {
        Exception exception = null;

        try {
            fileDeleteService.deleteFile(TEST_NAME);
        } catch(Exception ex){
            exception = ex;
        }

        File file = new File(TEST_DIRECTORY + TEST_NAME);
        assertThat(exception).isNull();
        assertThat(file.exists()).isFalse();
    }

    @Test
    public void shouldFailDeleteFile() {
        Exception exception = null;

        try {
            fileDeleteService.deleteFile("invalid");
        } catch(Exception ex){
            exception = ex;
        }

        File file = new File(TEST_DIRECTORY + TEST_NAME);
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(FileDeleteException.class);
        assertThat(file.exists()).isTrue();
    }

}
