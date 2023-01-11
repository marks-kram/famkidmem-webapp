package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.FileNotFoundException;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoServiceTest {

    private static final String LOGIN_HASH_NO_PERMISSION_2 = "loginHash";
    private static final String LOGIN_HASH_PERMISSION_2 = "loginHashWithPermission2";
    private static final String THUMBNAIL_BASE64 = "dGh1bWJuYWls";
    private static final String M3U8_BASE64 = "bTN1OA==";

    private UserEntity testUser;
    private ResponseBodyLogin testLoginNoPermission2;
    private ResponseBodyLogin testLoginPermission2;

    @Autowired
    private UserService userService;

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setup() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        editVideoService.addVideo(testUtils.createAddAnotherVideoRequest());
        prepareTest();
        testUtils.createTestFile("sequence.ts");
    }

    @After
    public void teardown(){
        Application.filesDir = "./files/";
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldGetIndex_NoPermission2(){
        Exception exception = null;
        ResponseBodyContentIndex contentIndex = null;

        try {
            contentIndex = videoService.getIndex(testLoginNoPermission2.getAccessToken());
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertIndex(contentIndex, false);
    }

    @Test
    public void shouldGetIndex_Permission2(){
        Exception exception = null;
        ResponseBodyContentIndex contentIndex = null;

        try {
            contentIndex = videoService.getIndex(testLoginPermission2.getAccessToken());
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertIndex(contentIndex, true);
    }

    @Test
    public void shouldGetIndexWithOrder() {
        Exception exception = null;
        ResponseBodyContentIndex contentIndex = null;
        prepareOrderTest();

        try {
            contentIndex = videoService.getIndex(testLoginNoPermission2.getAccessToken());
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertIndexWithOrder(contentIndex);
    }

    @Test
    public void shouldFailGetIndex(){
        Exception exception = null;

        try {
            videoService.getIndex("invalid");
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SecurityException.class);
    }

    @Test
    public void shouldGetThumbnail(){
        shouldGetFileBase64("thumbnail", THUMBNAIL_BASE64);
    }

    @Test
    public void shouldGetM3u8(){
        shouldGetFileBase64("m3u8", M3U8_BASE64);
    }

    @Test
    public void shouldFailGetFileBase64CausedByInvalidLogin(){
        shouldFailGetFileBase64("invalid", "thumbnail", SecurityException.class);
    }

    @Test
    public void shouldFailGetFileBase64CausedByFileNotFound(){
        shouldFailGetFileBase64(testLoginNoPermission2.getAccessToken(), "invalid", FileNotFoundException.class);
    }

    @Test
    public void shouldGetTsFile() {
        Exception exception = null;
        ResponseEntity response = null;

        try {
            response = videoService.getTsFile(testLoginNoPermission2.getAccessToken(), "sequence.ts");
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getHeaders().get("Content-Length").get(0)).isEqualTo("11");
        assertThat(response.getHeaders().get("Content-Type").get(0)).isEqualTo("video/vnd.dlna.mpeg-tts");
        assertThat(((ByteArrayResource)response.getBody()).getByteArray().length).isEqualTo(11);
    }

    @Test
    public void shouldFailGetTsFileCausedByInvalidLogin(){
        shouldFailGetTsFile("invalid", "sequence.ts", SecurityException.class);
    }

    @Test
    public void shouldFailGetTsFileCausedByFileNotFound(){
        shouldFailGetTsFile(testLoginNoPermission2.getAccessToken(), "invalid.ts", FileNotFoundException.class);
    }

    private void assertIndex(ResponseBodyContentIndex contentIndex, boolean userHasPermission2){
        assertThat(contentIndex).isNotNull();
        assertThat(contentIndex.getVideos().size()).isEqualTo(userHasPermission2 ? 2 : 1);
        assertThat(contentIndex.getVideos().get(0).getTitle()).isEqualTo("title/");
        if(userHasPermission2){
            assertThat(contentIndex.getVideos().get(1).getTitle()).isEqualTo("video2");
        }
        assertThat(contentIndex.getPersons().size()).isEqualTo(4);
        assertThat(contentIndex.getPersons().get(0)).isEqualTo("person1");
        assertThat(contentIndex.getPersons().get(1)).isEqualTo("person2");
        assertThat(contentIndex.getPersons().get(2)).isEqualTo("person3");
        assertThat(contentIndex.getPersons().get(3)).isEqualTo("person4");
        assertThat(contentIndex.getYears().get(0)).isEqualTo(1994);
        assertThat(contentIndex.getYears().get(1)).isEqualTo(1995);
        assertThat(contentIndex.getYears().get(2)).isEqualTo(1996);
        assertThat(contentIndex.getYears().get(3)).isEqualTo(1997);
        assertThat(contentIndex.getMasterKey()).isEqualTo(testUser.getMasterKey());
    }

    private void assertIndexWithOrder(ResponseBodyContentIndex contentIndex){
        assertThat(contentIndex).isNotNull();
        assertThat(contentIndex.getVideos().size()).isEqualTo(2);
        assertThat(contentIndex.getVideos().get(0).getTitle()).isEqualTo("video2");
        assertThat(contentIndex.getVideos().get(1).getTitle()).isEqualTo("title/");
        assertThat(contentIndex.getPersons().size()).isEqualTo(4);
        assertThat(contentIndex.getPersons().get(0)).isEqualTo("personFour");
        assertThat(contentIndex.getPersons().get(1)).isEqualTo("personOne");
        assertThat(contentIndex.getPersons().get(2)).isEqualTo("personThree");
        assertThat(contentIndex.getPersons().get(3)).isEqualTo("personTwo");
        assertThat(contentIndex.getYears().get(0)).isEqualTo(2001);
        assertThat(contentIndex.getYears().get(1)).isEqualTo(2002);
        assertThat(contentIndex.getYears().get(2)).isEqualTo(2003);
        assertThat(contentIndex.getYears().get(3)).isEqualTo(2004);
        assertThat(contentIndex.getVideos().get(1).getPersons().get(0).getName()).isEqualTo("personOne");
        assertThat(contentIndex.getVideos().get(1).getPersons().get(1).getName()).isEqualTo("personTwo");
        assertThat(contentIndex.getVideos().get(1).getYears().get(0).getValue()).isEqualTo(2002);
        assertThat(contentIndex.getVideos().get(1).getYears().get(1).getValue()).isEqualTo(2004);
    }

    private void shouldGetFileBase64(String filename, String base64){
        Exception exception = null;
        ResponseBodyContentFileBase64 content = null;

        try {
            content = videoService.getFileBase64(testLoginNoPermission2.getAccessToken(), filename);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(content).isNotNull();
        assertThat(content.getBase64()).isEqualTo(base64);
    }

    private void shouldFailGetFileBase64(String accessToken, String filename, Class exceptionClass){
        Exception exception = null;

        try {
            videoService.getFileBase64(accessToken, filename);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
    }

    private void shouldFailGetTsFile(String accessToken, String filename, Class exceptionClass){
        Exception exception = null;

        try {
            videoService.getTsFile(accessToken, filename);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
    }

    private void prepareTest() {
        testUser = testUtils.createTestUser(LOGIN_HASH_NO_PERMISSION_2, false);
        UserEntity user = testUtils.createTestUser(LOGIN_HASH_PERMISSION_2, true);
        testLoginNoPermission2 = userService.login(testUser.getUsername(), LOGIN_HASH_NO_PERMISSION_2, true);
        testLoginPermission2 = userService.login(user.getUsername(), LOGIN_HASH_PERMISSION_2, true);
    }

    private void prepareOrderTest(){
        RequestBodyUpdateVideo updateVideo1Request = testUtils.createUpdateVideoRequest();
        RequestBodyUpdateVideo updateVideo2Request = testUtils.createUpdateVideoRequest();
        updateVideo1Request.setTimestamp(System.currentTimeMillis()+80000);
        updateVideo1Request.setPersons(Arrays.asList("personTwo", "personOne"));
        updateVideo2Request.setPersons(Arrays.asList("personThree", "personFour"));
        updateVideo1Request.setYears(Arrays.asList(2004, 2002));
        updateVideo2Request.setYears(Arrays.asList(2001, 2003));
        updateVideo1Request.setTitle("title/");
        updateVideo1Request.setDesignator("title/");
        updateVideo2Request.setTitle("video2");
        updateVideo2Request.setDesignator("video2");
        try {
            editVideoService.updateVideo(updateVideo1Request);
            editVideoService.updateVideo(updateVideo2Request);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
