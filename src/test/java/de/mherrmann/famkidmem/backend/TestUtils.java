package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.*;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

@Service
public class TestUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private YearRepository yearRepository;

    @Autowired
    private VideoRepository videoRepository;


    public void dropAll() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        videoRepository.deleteAll();
        personRepository.deleteAll();
        yearRepository.deleteAll();
        fileRepository.deleteAll();
        keyRepository.deleteAll();
    }

    public void deleteTestFiles() {
        File directory = new File("./files");
        for (File file : directory.listFiles()) {
            file.delete();
        }
        directory.delete();
    }

    public UserEntity createTestUser(String loginHash) {
        String loginHashHash = Bcrypt.hash(loginHash);
        UserEntity testUser = new UserEntity("username", "", "salt", loginHashHash, "masterKey");
        testUser.setInit(true);
        testUser.setReset(true);
        return userRepository.save(testUser);
    }

    public RequestBodyAddUser createAddUserRequest() {
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setLoginHash("newLoginHash");
        addUserRequest.setMasterKey("newKey");
        addUserRequest.setPasswordKeySalt("newPasswordKeySalt");
        addUserRequest.setUsername("user");
        addUserRequest.setDisplayName("display");
        return addUserRequest;
    }

    public RequestBodyResetPassword createResetPasswordRequest(UserEntity testUser) {
        RequestBodyResetPassword resetPasswordRequest = new RequestBodyResetPassword();
        resetPasswordRequest.setLoginHash("modifiedLoginHash");
        resetPasswordRequest.setMasterKey("modifiedKey");
        resetPasswordRequest.setPasswordKeySalt("modifiedPasswordKeySalt");
        resetPasswordRequest.setUsername(testUser.getUsername());
        return resetPasswordRequest;
    }

    public RequestBodyDeleteUser createDeleteUserRequest(UserEntity testUser) {
        RequestBodyDeleteUser deleteUserRequest = new RequestBodyDeleteUser();
        deleteUserRequest.setUsername(testUser.getUsername());
        return deleteUserRequest;
    }

    public RequestBodyAddVideo createAddVideoRequest() throws IOException {
        return createAddAnotherVideoRequest(
                "title",
                "description",
                "key",
                "iv",
                "m3u8",
                "m3u8Key",
                "m3u8Iv",
                "thumbnail",
                "thumbnailKey",
                "thumbnailIv",
                "person1",
                "person2",
                1994,
                1995);
    }

    public RequestBodyAddVideo createAddAnotherVideoRequest() throws IOException {
        return createAddAnotherVideoRequest(
                "video2",
                "description",
                "key2",
                "iv2",
                "m3u82",
                "m3u8Key2",
                "m3u8Iv2",
                "thumbnail2",
                "thumbnailKey2",
                "thumbnailIv2",
                "person3",
                "person4",
                1996,
                1997);
    }

    private RequestBodyAddVideo createAddAnotherVideoRequest(
            String title, String description, String key, String iv, String m3u8, String m3u8Key, String m3u8Iv,
            String thumbnail, String thumbnailKey, String thumbnailIv, String person1, String person2, int year1, int year2)
                throws IOException {

        RequestBodyAddVideo addVideoRequest = new RequestBodyAddVideo();
        addVideoRequest.setTitle(title);
        addVideoRequest.setDescription(description);
        addVideoRequest.setKey(key);
        addVideoRequest.setIv(iv);
        addVideoRequest.setM3u8Filename(m3u8);
        addVideoRequest.setM3u8Key(m3u8Key);
        addVideoRequest.setM3u8Iv(m3u8Iv);
        addVideoRequest.setThumbnailFilename(thumbnail);
        addVideoRequest.setThumbnailKey(thumbnailKey);
        addVideoRequest.setThumbnailIv(thumbnailIv);
        addVideoRequest.setPersons(Arrays.asList(person1, person2));
        addVideoRequest.setYears(Arrays.asList(year1, year2));
        addVideoRequest.setRecordedInCologne(true);
        addVideoRequest.setRecordedInGardelgen(false);
        createTestFile(m3u8);
        createTestFile(thumbnail);
        return addVideoRequest;
    }

    public RequestBodyUpdateVideo createUpdateVideoRequest() {
        RequestBodyUpdateVideo updateVideoRequest = new RequestBodyUpdateVideo();
        updateVideoRequest.setDesignator("title");
        updateVideoRequest.setTitle("newTitle");
        updateVideoRequest.setDescription("newDescription");
        updateVideoRequest.setKey("newKey");
        updateVideoRequest.setIv("newIv");
        updateVideoRequest.setThumbnailKey("newThumbnailKey");
        updateVideoRequest.setThumbnailIv("newThumbnailIv");
        updateVideoRequest.setPersons(Arrays.asList("person2", "person3"));
        updateVideoRequest.setYears(Arrays.asList(1994, 1997));
        updateVideoRequest.setRecordedInCologne(false);
        updateVideoRequest.setRecordedInGardelgen(true);
        return updateVideoRequest;
    }

    private void createTestFile(String filename) throws IOException {
        new File("./files").mkdir();
        new File("./files/"+filename).createNewFile();
        FileWriter myWriter = new FileWriter("./files/"+filename);
        myWriter.write(filename);
        myWriter.close();
    }

}
