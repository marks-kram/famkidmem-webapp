package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.AddMessageRequest;
import de.mherrmann.famkidmem.backend.entity.ChatMessage;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.ChatMessageRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final KeyRepository keyRepository;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository, KeyRepository keyRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.keyRepository = keyRepository;
    }

    public void addMessage (AddMessageRequest addMessageRequest, UserEntity user) {
        Key key = new Key(addMessageRequest.getKey(), addMessageRequest.getIv());
        key = keyRepository.save(key);
        ChatMessage message = new ChatMessage(addMessageRequest.getMessage(), user, key);
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getAllMessages () {
        Iterable<ChatMessage> messagesIterable = chatMessageRepository.findAllByOrderByTimestampAsc();
        List<ChatMessage> messages = new ArrayList<>();
        messagesIterable.forEach(messages::add);
        return messages;
    }

    public List<ChatMessage> getMessagesSince (long threshold) {
        Iterable<ChatMessage> messagesIterable = chatMessageRepository.findAllByTimestampAfterOrderByTimestampAsc(threshold);
        List<ChatMessage> messages = new ArrayList<>();
        messagesIterable.forEach(messages::add);
        return messages;
    }
}
