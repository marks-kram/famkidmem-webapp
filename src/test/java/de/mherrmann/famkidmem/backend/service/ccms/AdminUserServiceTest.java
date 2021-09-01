package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.AddEntityException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminUserServiceTest {

    private static final String LOGIN_HASH = "loginHash";

    private UserEntity testUser;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        createUser();
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddUser(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        Exception exception = null;

        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(userRepository.existsByUsername("user")).isTrue();
        UserEntity user = userRepository.findByUsername("user").get();
        assertThat(Bcrypt.check("newLoginHash", user.getLoginHashHash())).isTrue();
        assertThat(user.getMasterKey()).isEqualTo("newKey");
        assertThat(user.getPasswordKeySalt()).isEqualTo("newPasswordKeySalt");
        assertThat(user.getDisplayName()).isEqualTo("display");
    }

    @Test
    public void shouldFailAddUserCausedByUserAlreadyExists() {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        shouldFailAddUser(addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByEmptyDisplayName() {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setDisplayName("");

        shouldFailAddUser(addUserRequest);
    }

    @Test
    public void shouldDeleteUser(){
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testUser);
        Exception exception = null;

        try {
            adminUserService.deleteUser(deleteUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(userRepository.existsByUsername(testUser.getUsername())).isFalse();
    }

    @Test
    public void shouldFailDeleteUserCausedByUserBotFound(){
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testUser);
        deleteUserRequest.setUsername("wrong");

        shouldFailDeleteUser(EntityNotFoundException.class, deleteUserRequest);
    }

    @Test
    public void shouldResetPassword(){
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testUser);
        Exception exception = null;

        try {
            adminUserService.resetPassword(resetPasswordRequest);
        } catch (Exception ex){
            exception = ex;
        }

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        assertThat(exception).isNull();
        assertThat(Bcrypt.check(resetPasswordRequest.getLoginHash(), user.getLoginHashHash())).isTrue();
        assertThat(user.getPasswordKeySalt()).isEqualTo(resetPasswordRequest.getPasswordKeySalt());
        assertThat(user.getMasterKey()).isEqualTo(resetPasswordRequest.getMasterKey());
        assertThat(user.isInit()).isFalse();
        assertThat(user.isReset()).isTrue();
    }

    @Test
    public void shouldFailResetPasswordCausedByUserNotFound(){
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testUser);
        resetPasswordRequest.setUsername("wrong");
        Exception exception = null;

        try {
            adminUserService.resetPassword(resetPasswordRequest);
        } catch (Exception ex){
            exception = ex;
        }

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(EntityNotFoundException.class);
        assertThat(Bcrypt.check(resetPasswordRequest.getLoginHash(), user.getLoginHashHash())).isFalse();
        assertThat(user.getPasswordKeySalt()).isNotEqualTo(resetPasswordRequest.getPasswordKeySalt());
        assertThat(user.getMasterKey()).isNotEqualTo(resetPasswordRequest.getMasterKey());
        assertThat(user.isInit()).isFalse();
        assertThat(user.isReset()).isFalse();
    }

    @Test
    public void shouldGetUsers(){
        ResponseBodyGetUsers usersResponse = null;
        Exception exception = null;

        try {
            usersResponse = adminUserService.getUsers();
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(usersResponse).isNotNull();
        assertThat(usersResponse.getUsers()).isNotEmpty();
        assertThat(usersResponse.getUsers().get(0).getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    public void shouldSetPermission2_enable(){
        testUser.setPermission2(false);
        userRepository.save(testUser);
        Exception exception = null;

        try {
            adminUserService.setPermission2(testUser.getUsername(), true);
        } catch (Exception ex){
            exception = ex;
        }

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        assertThat(exception).isNull();
        assertThat(user.isPermission2()).isTrue();
    }

    @Test
    public void shouldSetPermission2_disable(){
        testUser.setPermission2(true);
        userRepository.save(testUser);
        Exception exception = null;

        try {
            adminUserService.setPermission2(testUser.getUsername(), false);
        } catch (Exception ex){
            exception = ex;
        }

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        assertThat(exception).isNull();
        assertThat(user.isPermission2()).isFalse();
    }

    private void shouldFailAddUser(RequestBodyAddUser addUserRequest){
        long countBefore = userRepository.count();
        Exception exception = null;

        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(AddEntityException.class);
        assertThat(userRepository.count()).isEqualTo(countBefore);
    }

    private void shouldFailDeleteUser(Class exceptionClass, RequestBodyDeleteUser deleteUserRequest){
        Exception exception = null;

        try {
            adminUserService.deleteUser(deleteUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
        assertThat(userRepository.existsByUsername(testUser.getUsername())).isTrue();
    }

    private void createUser() {
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("username", "displayName", "salt", loginHashHash, "masterKey", false);
        testUser.setInit(false);
        testUser.setReset(false);
        userRepository.save(testUser);
    }

    private RequestBodyAddUser createAddUserRequest(){
        return testUtils.createAddUserRequest();
    }

}
