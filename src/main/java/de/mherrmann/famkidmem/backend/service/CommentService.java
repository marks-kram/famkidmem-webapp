package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.AddCommentRequest;
import de.mherrmann.famkidmem.backend.body.RemoveCommentRequest;
import de.mherrmann.famkidmem.backend.body.UpdateCommentRequest;
import de.mherrmann.famkidmem.backend.entity.Comment;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.CommentRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.utils.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public String addComment(AddCommentRequest addCommentRequest, UserEntity user) throws EntityNotFoundException {
        String videoTitle = addCommentRequest.getVideoTitle();
        Optional<Video> videoOptional = videoRepository.findByTitle(videoTitle);

        if (!videoOptional.isPresent()) {
            LOGGER.error("Could not add comment for Video. Video not found. title: {}", videoTitle);
            throw new EntityNotFoundException(Video.class, videoTitle);
        }

        Key key = new Key(addCommentRequest.getKey(), addCommentRequest.getIv());
        key = keyRepository.save(key);

        Comment comment = new Comment(addCommentRequest.getText(), user, videoOptional.get(), key);
        return commentRepository.save(comment).getCid();
    }

    public List<Comment> getComments (String videoTitle) throws EntityNotFoundException {
        videoTitle = ConversionUtil.base64urlToBase64(videoTitle);
        Optional<Video> videoOptional = videoRepository.findByTitle(videoTitle);

        if (!videoOptional.isPresent()) {
            LOGGER.error("Could not get comments for Video. Video not found. title: {}", videoTitle);
            throw new EntityNotFoundException(Video.class, videoTitle);
        }

        List<Comment> comments = new ArrayList<>();
        Iterable<Comment> commentIterable = commentRepository.findAllByVideo(videoOptional.get());
        commentIterable.forEach(comments::add);
        return comments;
    }

    public void updateComment(UpdateCommentRequest updateCommentRequest, UserEntity user) throws EntityNotFoundException {
        String videoTitle = updateCommentRequest.getVideoTitle();
        String cid = updateCommentRequest.getCid();
        Comment comment = getComment(user, videoTitle, cid);
        comment.setText(updateCommentRequest.getText());
        comment.setModifiedTrue();
        comment.setModificationToNow();
        commentRepository.save(comment);
    }

    public void removeComment (RemoveCommentRequest removeCommentRequest, UserEntity user) throws EntityNotFoundException {
        String cid = removeCommentRequest.getVideoTitle();
        String text = removeCommentRequest.getCid();
        Comment comment = getComment(user, cid, text);
        comment.setModificationToNow();
        comment.setText(null);
        comment.setRemovedTrue();
        commentRepository.save(comment);
    }

    private Comment getComment(UserEntity user, String videoTitle, String cid) throws EntityNotFoundException {
        Optional<Video> videoOptional = videoRepository.findByTitle(videoTitle);

        if (!videoOptional.isPresent()) {
            LOGGER.error("Could not update comment or remove for Video. Video not found. title: {}", videoTitle);
            throw new EntityNotFoundException(Video.class, videoTitle);
        }

        Optional<Comment> commentOptional = commentRepository.findByVideoAndUserAndCid(videoOptional.get(), user, cid);

        if (!commentOptional.isPresent()) {
            LOGGER.error("Could not update or remove comment. Comment not found. video title: {}; cid: {}", videoTitle, cid);
            throw new EntityNotFoundException(Comment.class, cid);
        }

        return commentOptional.get();
    }
}
