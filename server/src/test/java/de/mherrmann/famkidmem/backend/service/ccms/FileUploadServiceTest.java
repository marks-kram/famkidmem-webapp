package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.exception.FileUploadException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadServiceTest {

    private static final String TEST_CONTENT = "Content";
    private static final String TEST_CHANGED_CONTENT = "Changed Content";
    private static final String TEST_NAME = "test.txt";
    private static final String TEST_DIRECTORY = "./files/";

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setUp(){
        testUtils.createTestFilesDirectory();
    }

    @After
    public void tearDown(){
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());
        Exception exception = null;

        try {
            fileUploadService.store(multipartFile);
        } catch(Exception ex){
            exception = ex;
        }

        File file = new File(TEST_DIRECTORY + TEST_NAME);
        assertThat(exception).isNull();
        assertThat(file.exists()).isTrue();
        assertThat(new String(Files.readAllBytes(file.toPath()))).isEqualTo(TEST_CONTENT);
    }

    @Test
    public void shouldOverrideFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());
        Exception exception = null;

        try {
            fileUploadService.store(multipartFile);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CHANGED_CONTENT.getBytes());

        try {
            fileUploadService.store(multipartFile);
        } catch(Exception ex){
            exception = ex;
        }

        File file = new File(TEST_DIRECTORY + TEST_NAME);
        assertThat(exception).isNull();
        assertThat(file.exists()).isTrue();
        assertThat(new String(Files.readAllBytes(file.toPath()))).isEqualTo(TEST_CHANGED_CONTENT);
    }

    @Test
    public void shouldFailCausedByEmptyName(){
        MockMultipartFile multipartFile = new MockMultipartFile("file", "",
                "text/plain", TEST_CONTENT.getBytes());

        shouldFail(multipartFile);
    }

    @Test
    public void shouldFailCausedByEmptyContent(){
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", new byte[]{});

        shouldFail(multipartFile);
    }

    @Test
    public void shouldFailCausedByIOError(){
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());
        testUtils.deleteTestFiles();

        shouldFail(multipartFile);
    }

    private void shouldFail(MockMultipartFile multipartFile){
        Exception exception = null;
        File directory = new File(TEST_DIRECTORY);
        int filesBefore = directory.exists() ? directory.list().length : -1;

        try {
            fileUploadService.store(multipartFile);
        } catch(Exception ex){
            exception = ex;
        }

        int filesNow = directory.exists() ? directory.list().length : -1;
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(FileUploadException.class);
        assertThat(filesNow).isEqualTo(filesBefore);
    }

}
