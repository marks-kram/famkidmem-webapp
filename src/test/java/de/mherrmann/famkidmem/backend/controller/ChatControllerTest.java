package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.*;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyMessages;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.ChatMessageRepository;
import de.mherrmann.famkidmem.backend.service.ChatService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatService chatService;

    @Autowired
    private TestUtils testUtils;

    private UserEntity user;

    private String accessToken;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Before
    public void setup() throws Exception {
        testUtils.dropAll();
        user = testUtils.createTestUser("loginHash", false);
        accessToken = testUtils.createUserSession(user.getUsername(), "loginHash");
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddMessage() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/chat/add/{accessToken}", accessToken)
                        .contentType("application/json")
                        .content(asJsonString(createAddMessageRequest()))
                ).andExpect(status().isCreated()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("success");
        assertThat(body.getDetails()).isEqualTo("chat message added");
        assertThat(body.getException()).isNull();
        assertThat(chatMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllMessages() throws Exception {
        AddMessageRequest addMessageRequest = createAddMessageRequest();
        chatService.addMessage(addMessageRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/chat/get-all/{accessToken}", accessToken))
                .andExpect(status().isOk())
                .andReturn();

        ResponseBodyMessages body = jsonToResponseBodyMessages(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("success");
        assertThat(body.getDetails()).isEqualTo("");
        assertThat(body.getMessages()).hasSize(1);
        assertThat(body.getMessages().get(0).getMessage()).isEqualTo(addMessageRequest.getMessage());
        assertThat(body.getUserKey()).isEqualTo(user.getMasterKey());
        assertThat(body.getException()).isNull();
    }

    @Test
    public void shouldGetTheNewMessage() throws Exception {
        AddMessageRequest addMessageRequest = createAddMessageRequest();
        chatService.addMessage(addMessageRequest, user);
        long now = System.currentTimeMillis();
        long thresholdBefore = now - 2000;
        long thresholdAfter = now + 2000;

        MvcResult mvcResult1 = this.mockMvc.perform(get("/api/chat/get-since/{threshold}/{accessToken}", thresholdBefore, accessToken))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult mvcResult2 = this.mockMvc.perform(get("/api/chat/get-since/{threshold}/{accessToken}", thresholdAfter, accessToken))
                .andExpect(status().isOk())
                .andReturn();

        ResponseBodyMessages body1 = jsonToResponseBodyMessages(mvcResult1.getResponse().getContentAsString());
        ResponseBodyMessages body2 = jsonToResponseBodyMessages(mvcResult2.getResponse().getContentAsString());
        assertThat(body1.getMessage()).isEqualTo("success");
        assertThat(body1.getDetails()).isEqualTo("");
        assertThat(body1.getException()).isNull();
        assertThat(body1.getMessages()).hasSize(1);
        assertThat(body1.getMessages().get(0).getMessage()).isEqualTo(addMessageRequest.getMessage());
        assertThat(body2.getMessage()).isEqualTo("success");
        assertThat(body2.getDetails()).isEqualTo("");
        assertThat(body2.getException()).isNull();
        assertThat(body2.getMessages()).isEmpty();
        assertThat(body1.getUserKey()).isEqualTo(user.getMasterKey());
        assertThat(body2.getUserKey()).isEqualTo(user.getMasterKey());
    }


    @Test
    public void shouldFailAddMessageDueToInvalidAccessToken() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/chat/add/{accessToken}", "invalid")
                .contentType("application/json")
                .content(asJsonString(createAddMessageRequest()))
        ).andExpect(status().isBadRequest()).andReturn();

        ResponseBody body = jsonToResponseBody(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("chat message not added");
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
        assertThat(chatMessageRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFailGetAllMessagesDueToInvalidAccessToken() throws Exception {
        AddMessageRequest addMessageRequest = createAddMessageRequest();
        chatService.addMessage(addMessageRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/chat/get-all/{accessToken}", "invalid"))
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseBodyMessages body = jsonToResponseBodyMessages(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("could not get chat messages");
        assertThat(body.getMessages()).isNull();
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
        assertThat(body.getUserKey()).isNull();
    }

    @Test
    public void shouldFailGetMessagesSinceDueToInvalidAccessToken() throws Exception {
        AddMessageRequest addMessageRequest = createAddMessageRequest();
        chatService.addMessage(addMessageRequest, user);

        MvcResult mvcResult = this.mockMvc.perform(get("/api/chat/get-since/{threshold}/{accessToken}", 0L, "invalid"))
                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseBodyMessages body = jsonToResponseBodyMessages(mvcResult.getResponse().getContentAsString());
        assertThat(body.getMessage()).isEqualTo("error");
        assertThat(body.getDetails()).isEqualTo("could not get chat messages");
        assertThat(body.getMessages()).isNull();
        assertThat(body.getException()).isEqualTo(SecurityException.class.getSimpleName());
        assertThat(body.getUserKey()).isNull();
    }

    private AddMessageRequest createAddMessageRequest() {
        AddMessageRequest addMessageRequest = new AddMessageRequest();
        addMessageRequest.setMessage("test");
        addMessageRequest.setKey("key");
        addMessageRequest.setIv("iv");
        return addMessageRequest;
    }

    private static ResponseBody jsonToResponseBody(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBodyMessages jsonToResponseBodyMessages(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyMessages.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
