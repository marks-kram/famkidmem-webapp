package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.ChatMessage;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {

    Iterable<ChatMessage> findAllByTimestampAfter(long threshold);
}
