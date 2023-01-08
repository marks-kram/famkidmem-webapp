package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.AddMessageRequest;
import de.mherrmann.famkidmem.backend.entity.ChatMessage;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.ChatMessageRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
