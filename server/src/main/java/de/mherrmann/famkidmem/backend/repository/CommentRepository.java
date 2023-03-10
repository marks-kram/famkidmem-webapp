package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, String> {
    Iterable<Comment> findAllByVideoOrderByCreationAsc(Video video);

    Optional<Comment> findByUserAndCid(UserEntity user, String text);
}
