package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.Video;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, String> {
    Iterable<Comment> findAllByVideo(Video video);
}
