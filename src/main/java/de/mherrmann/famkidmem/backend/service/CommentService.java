package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.AddCommentRequest;
import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.CommentRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final KeyRepository keyRepository;
    private final VideoRepository videoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    public CommentService(CommentRepository commentRepository, KeyRepository keyRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.keyRepository = keyRepository;
        this.videoRepository = videoRepository;
    }

    public void addComment(AddCommentRequest addCommentRequest, UserEntity user) throws EntityNotFoundException {
        String videoTitle = addCommentRequest.getVideoTitle();
        Optional<Video> videoOptional = videoRepository.findByTitle(videoTitle);

        if (!videoOptional.isPresent()) {
            LOGGER.error("Could not add comment for Video. Video not found. title: {}", videoTitle);
            throw new EntityNotFoundException(Video.class, videoTitle);
        }

        Key key = new Key(addCommentRequest.getKey(), addCommentRequest.getIv());
        key = keyRepository.save(key);

        Comment comment = new Comment(addCommentRequest.getText(), user, videoOptional.get(), key);
        commentRepository.save(comment);
    }
}
