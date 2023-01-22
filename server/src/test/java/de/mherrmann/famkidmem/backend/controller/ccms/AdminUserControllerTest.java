package de.mherrmann.famkidmem.backend.controller.ccms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.ccms.AdminUserService;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AdminUserControllerTest {



    @Autowired
    private MockMvc mockMvc;

    private static final String LOGIN_HASH = "loginHash";

    private UserEntity testUser;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() throws IOException {
        createUser();
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown() {
        testUtils.dropAll();
        testUtils.deleteAuthTokenHashFile();
    }

    @Test
    public void shouldAddUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/admin/user/add")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createAddUserRequest())))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully added user: " + createAddUserRequest().getUsername());
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    public void shouldFailAddUserCausedByUserAlreadyExists() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        shouldFailAddUser("User with username already exist: user", addUserRequest, 2);
    }

    @Test
    public void shouldFailAddUserCausedByEmptyDisplayName() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setDisplayName("");

        shouldFailAddUser("Display name can not be empty.", addUserRequest, 1);
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testUser);
        MvcResult mvcResult = this.mockMvc.perform(delete("/ccms/admin/user/delete")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(deleteUserRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully deleted user: " + deleteUserRequest.getUsername());
        assertThat(userRepository.existsByUsername(deleteUserRequest.getUsername())).isFalse();
    }

    @Test
    public void shouldFailDeleteUserCausedByUserNotFound() throws Exception {
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testUser);
        deleteUserRequest.setUsername("wrong");

        shouldFailDeleteUser("Entity does not exist. Type: UserEntity; designator: wrong", deleteUserRequest);
    }

    @Test
    public void shouldResetPassword() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testUser);
        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/admin/user/reset")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPasswordRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully reset password for user: " + resetPasswordRequest.getUsername());
        assertThat(userRepository.findByUsername(testUser.getUsername()).get().getMasterKey()).isEqualTo(resetPasswordRequest.getMasterKey());
    }

    @Test
    public void shouldFailResetPasswordCausedByUserNotFound() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testUser);
        resetPasswordRequest.setUsername("wrong");

        shouldFailResetPassword("Entity does not exist. Type: UserEntity; designator: wrong", resetPasswordRequest);
    }

    @Test
    public void shouldGetUsers() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/admin/user/get/")
                .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        ResponseBodyGetUsers usersResponse = jsonToUsersResponse(mvcResult.getResponse().getContentAsString());
        String message = usersResponse.getMessage();
        String details = usersResponse.getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully got users");
        assertThat(usersResponse.getUsers()).isNotNull();
        assertInternalThingsKeptInternal(usersResponse);
    }

    @Test
    public void shouldEnablePermission2() throws Exception {
        testUser.setPermission2(false);
        userRepository.save(testUser);

        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/admin/user/enablePermission2/{username}", testUser.getUsername())
                        .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully enabled permission2 for user: " + testUser.getUsername());
        assertThat(user.isPermission2()).isTrue();
    }

    @Test
    public void shouldDisablePermission2() throws Exception {
        testUser.setPermission2(true);
        userRepository.save(testUser);

        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/admin/user/disablePermission2/{username}", testUser.getUsername())
                        .header("CCMS-AUTH-TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully disabled permission2 for user: " + testUser.getUsername());
        assertThat(user.isPermission2()).isFalse();
    }

    private void assertInternalThingsKeptInternal(ResponseBodyGetUsers usersResponse){
        assertThat(usersResponse.getUsers().get(0).getId()).isNull();
        assertThat(usersResponse.getUsers().get(0).getMasterKey()).isNull();
        assertThat(usersResponse.getUsers().get(0).getLoginHashHash()).isNull();
        assertThat(usersResponse.getUsers().get(0).getPasswordKeySalt()).isNull();
    }

    private void shouldFailAddUser(String expectedDetails, RequestBodyAddUser addUserRequest, int users) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/admin/user/add")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addUserRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.count()).isEqualTo(users);
    }

    private void shouldFailDeleteUser(String expectedDetails, RequestBodyDeleteUser deleteUserRequest) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete("/ccms/admin/user/delete")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(deleteUserRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.existsByUsername(testUser.getUsername())).isTrue();
    }

    private void shouldFailResetPassword(String expectedDetails, RequestBodyResetPassword resetPasswordRequest) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/ccms/admin/user/reset")
                .header("CCMS-AUTH-TOKEN", "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPasswordRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.findByUsername(testUser.getUsername()).get().getMasterKey()).isNotEqualTo(resetPasswordRequest.getMasterKey());
    }


    private RequestBodyAddUser createAddUserRequest(){
        return testUtils.createAddUserRequest();
    }


    private void createUser() {
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("admin", "", "salt", loginHashHash, "masterKey", false);
        testUser.setInit(false);
        testUser.setReset(false);
        userRepository.save(testUser);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBody jsonToResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBodyGetUsers jsonToUsersResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyGetUsers.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
