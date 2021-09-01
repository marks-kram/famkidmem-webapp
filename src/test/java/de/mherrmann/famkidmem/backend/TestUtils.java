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
        if(!directory.exists()){
            return;
        }
        for (File file : directory.listFiles()) {
            file.delete();
        }
        directory.delete();
    }

    public UserEntity createTestUser(String loginHash) {
        return createTestUser(loginHash, false);
    }

    public UserEntity createTestUser(String loginHash, boolean permission2) {
        String loginHashHash = Bcrypt.hash(loginHash);
        UserEntity testUser = new UserEntity(loginHash, "", "salt", loginHashHash, "masterKey", permission2);
        testUser.setInit(true);
        testUser.setReset(true);
        testUser.setMasterKey("masterKey");
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
        return createAddVideoRequest(
                "title",
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
                1995,
                System.currentTimeMillis(),
                false);
    }

    public RequestBodyAddVideo createAddAnotherVideoRequest() throws IOException {
        return createAddVideoRequest(
                "video2",
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
                1997,
                System.currentTimeMillis()+60000,
                true);
    }

    private RequestBodyAddVideo createAddVideoRequest(
            String title, String key, String iv, String m3u8, String m3u8Key, String m3u8Iv, String thumbnail,
            String thumbnailKey, String thumbnailIv, String person1, String person2, int year1, int year2, long time, boolean permission2)
                throws IOException {

        RequestBodyAddVideo addVideoRequest = new RequestBodyAddVideo();
        addVideoRequest.setTitle(title);
        addVideoRequest.setDescription("description");
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
        addVideoRequest.setRecordedInGardelegen(false);
        addVideoRequest.setTimestamp(time);
        addVideoRequest.setShowDateValues(2);
        addVideoRequest.setPermission2(permission2);
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
        updateVideoRequest.setRecordedInGardelegen(true);
        updateVideoRequest.setTimestamp(System.currentTimeMillis());
        updateVideoRequest.setShowDateValues(2);
        return updateVideoRequest;
    }

    public void createTestFile(String filename) throws IOException {
        createTestFilesDirectory();
        new File("./files/"+filename).createNewFile();
        FileWriter fileWriter = new FileWriter("./files/"+filename);
        fileWriter.write(filename);
        fileWriter.close();
    }

    public void createAuthTokenHashFile() throws IOException {
        createTestFilesDirectory();
        new File("./files/ccms_auth_token_hash").createNewFile();
        FileWriter fileWriter = new FileWriter("./files/ccms_auth_token_hash");
        fileWriter.write("$2a$13$3c7JEfBO9uTpvILcHFj4tuDR7krJQPgFMH7vzPRIMU3eLZL9NgKMS");
        fileWriter.close();
    }

    public void deleteAuthTokenHashFile() {
        new File("./files/ccms_auth_token_hash").delete();
    }

    public void createTestFilesDirectory(){
        new File("./files").mkdir();
    }

}
