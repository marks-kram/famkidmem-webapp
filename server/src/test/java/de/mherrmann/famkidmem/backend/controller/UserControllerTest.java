package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.RequestBodyLogin;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangePassword;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangeUsername;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangeUsernameAndPassword;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedLogout;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.LockService;
import de.mherrmann.famkidmem.backend.service.UserService;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @MockBean
    private LockService lockService;

    private UserEntity testUser;

    private static final String LOGIN_HASH = "loginHash";

    @Before
    public void setup() {
        createTestUser();
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldLoginPermanent() throws Exception {
        shouldLogin(true);
    }

    @Test
    public void shouldLoginSession() throws Exception {
        shouldLogin(false);
    }

    @Test
    public void shouldFailLoginCausedByInvalidCredentials() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogin(false, true))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String ex = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getException();
        String message = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(sessionRepository.count()).isEqualTo(0);
        assertThat(message).isEqualTo("error");
        assertThat(ex).isEqualTo("LoginException");
        assertThat(details).isEqualTo("Username or Password is wrong");
    }

    @Test
    public void shouldFailLoginCausedByLock() throws Exception {
        given(lockService.isLocked(testUser.getUsername())).willReturn(true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogin(true, true))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String ex = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getException();
        String message = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(ex).isEqualTo("LockException");
        assertThat(details).isEqualTo("This user is locked for login. Too much invalid login attempts");
    }

    @Test
    public void shouldLogout() throws Exception {
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogout(login.getAccessToken()))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(sessionRepository.findByAccessToken(login.getAccessToken()).isPresent()).isFalse();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Logout was successful");
    }

    @Test
    public void shouldFailLogout() throws Exception {
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogout("wrong"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(sessionRepository.findByAccessToken(login.getAccessToken()).isPresent()).isTrue();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: logout user");
    }

    @Test
    public void shouldChangeUsername() throws Exception {
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createUsernameChange(login.getAccessToken()))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        Optional<UserEntity> userOptional = userRepository.findByUsername("newValue");
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully changed username");
        assertThat(userOptional.isPresent()).isTrue();
    }

    @Test
    public void shouldFailChangeUsername() throws Exception {
        userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createUsernameChange("wrong"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        Optional<UserEntity> userOptional = userRepository.findByUsername("newValue");
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: change username and/or password");
        assertThat(userOptional.isPresent()).isFalse();
    }

    @Test
    public void shouldChangePassword() throws Exception {
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createPasswordChange(login.getAccessToken()))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        String loginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully changed password");
        assertThat(Bcrypt.check("newValue", loginHashHash)).isTrue();
    }

    @Test
    public void shouldFailChangePassword() throws Exception {
        userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createPasswordChange("wrong"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        String loginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: change username and/or password");
        assertThat(Bcrypt.check("newValue", loginHashHash)).isFalse();
    }

    @Test
    public void shouldChangeUsernameAndPassword() throws Exception {
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/both")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createUsernameAndPasswordChange(login.getAccessToken()))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        String loginHashHash = userRepository.findByUsername("newUsername").get().getLoginHashHash();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully changed username and password");
        assertThat(Bcrypt.check("newValue", loginHashHash)).isTrue();
    }

    @Test
    public void shouldFailChangeUsernameAndPassword() throws Exception {
        userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/both")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createUsernameAndPasswordChange("wrong"))))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        String loginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: change username and/or password");
        assertThat(Bcrypt.check("newValue", loginHashHash)).isFalse();
    }

    @Test
    public void shouldGetMasterKey() throws Exception {
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH, true);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/user/key/{accessToken}", login.getAccessToken()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("masterKey");
    }

    @Test
    public void shouldFailGetMasterKey() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/user/key/invalid"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        String exception = jsonToResponse(mvcResult.getResponse().getContentAsString()).getException();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: get masterKey");
        assertThat(exception).isEqualTo("SecurityException");
    }

    private void shouldLogin(boolean permanent) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogin(true, permanent))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String accessToken = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getAccessToken();
        String message = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Login was successful");
        assertThat(sessionRepository.findByAccessToken(accessToken).isPresent()).isTrue();
        assertThat(sessionRepository.findByAccessToken(accessToken).get().isPermanent()).isEqualTo(permanent);
    }

    private void createTestUser() {
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("username", "displayName","salt", loginHashHash, "masterKey", false);
        userRepository.save(testUser);
    }

    private RequestBodyLogin createLogin(boolean valid, boolean permanent){
        RequestBodyLogin login = new RequestBodyLogin();
        login.setLoginHash(valid ? LOGIN_HASH : "wrong");
        login.setUsername(testUser.getUsername());
        login.setPermanent(permanent);
        return login;
    }

    private RequestBodyAuthorizedLogout createLogout(String accessToken){
        RequestBodyAuthorizedLogout logout = new RequestBodyAuthorizedLogout();
        logout.setAccessToken(accessToken);
        return logout;
    }

    private RequestBodyAuthorizedChangeUsername createUsernameChange(String accessToken){
        RequestBodyAuthorizedChangeUsername usernameChange = new RequestBodyAuthorizedChangeUsername();
        usernameChange.setAccessToken(accessToken);
        usernameChange.setNewUsername("newValue");
        return usernameChange;
    }

    private RequestBodyAuthorizedChangePassword createPasswordChange(String accessToken){
        RequestBodyAuthorizedChangePassword passwordChange = new RequestBodyAuthorizedChangePassword();
        passwordChange.setAccessToken(accessToken);
        passwordChange.setNewLoginHash("newValue");
        passwordChange.setNewPasswordKeySalt("newSalt");
        passwordChange.setNewMasterKey("key");
        return passwordChange;
    }

    private RequestBodyAuthorizedChangeUsernameAndPassword createUsernameAndPasswordChange(String accessToken){
        RequestBodyAuthorizedChangeUsernameAndPassword usernameAndPasswordChange = new RequestBodyAuthorizedChangeUsernameAndPassword();
        usernameAndPasswordChange.setNewUsername("newUsername");
        usernameAndPasswordChange.setAccessToken(accessToken);
        usernameAndPasswordChange.setNewLoginHash("newValue");
        usernameAndPasswordChange.setNewPasswordKeySalt("newSalt");
        usernameAndPasswordChange.setNewMasterKey("key");
        return usernameAndPasswordChange;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBodyLogin jsonToLoginResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyLogin.class);
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
}
