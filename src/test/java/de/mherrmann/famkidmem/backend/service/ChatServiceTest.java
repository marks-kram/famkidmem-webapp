package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.AddMessageRequest;
import de.mherrmann.famkidmem.backend.entity.ChatMessage;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.ChatMessageRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private TestUtils testUtils;

    private UserEntity user;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Before
    public void setup() throws Exception {
        testUtils.dropAll();
        user = testUtils.createTestUser("loginHash", false);
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddMessage() {
        AddMessageRequest addMessageRequest = createAddMessageRequest();

        chatService.addMessage(addMessageRequest, user);

        assertThat(chatMessageRepository.count()).isEqualTo(1);
        assertThat(chatMessageRepository.findAll().iterator()).hasNext();
        ChatMessage message = chatMessageRepository.findAll().iterator().next();
        assertThat(message.getMessage()).isEqualTo(addMessageRequest.getMessage());
        assertThat(message.getUser().getUsername()).isEqualTo(user.getUsername());
    }

   @Test
    public void shouldGet2Messages() {
        AddMessageRequest addMessageRequest1 = createAddMessageRequest();
        AddMessageRequest addMessageRequest2 = createAddMessageRequest();
        addMessageRequest1.setMessage("text1");
        addMessageRequest2.setMessage("text2");
        chatService.addMessage(addMessageRequest1, user);
        chatService.addMessage(addMessageRequest2, user);

        List<ChatMessage> messages = chatService.getAllMessages();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getMessage()).isEqualTo(addMessageRequest1.getMessage());
        assertThat(messages.get(1).getMessage()).isEqualTo(addMessageRequest2.getMessage());
    }

    @Test
    public void shouldGetTheNewMessage() {
        AddMessageRequest addMessageRequest = createAddMessageRequest();
        chatService.addMessage(addMessageRequest, user);

        List<ChatMessage> messages1 = chatService.getMessagesSince(System.currentTimeMillis() - 2000);
        List<ChatMessage> messages2 = chatService.getMessagesSince(System.currentTimeMillis() + 2000);

        assertThat(messages1).hasSize(1);
        assertThat(messages2).isEmpty();
        assertThat(messages1.get(0).getMessage()).isEqualTo(addMessageRequest.getMessage());
    }

    private AddMessageRequest createAddMessageRequest() {
        AddMessageRequest addMessageRequest = new AddMessageRequest();
        addMessageRequest.setMessage("test");
        addMessageRequest.setKey("key");
        addMessageRequest.setIv("iv");
        return addMessageRequest;
    }
}
