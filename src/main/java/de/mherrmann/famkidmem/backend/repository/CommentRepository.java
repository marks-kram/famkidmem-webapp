package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, String> {
    Iterable<Comment> findAllByVideo(Video video);

    Optional<Comment> findByVideoAndUserAndText(Video video, UserEntity user, String text);

    @Transactional
    void deleteByVideoAndUserAndText(Video video, UserEntity user, String text);
}
