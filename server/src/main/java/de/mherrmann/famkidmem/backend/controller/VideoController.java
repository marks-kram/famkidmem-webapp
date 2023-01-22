package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.backend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/video")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(value = "/index/{accessToken}")
    public ResponseEntity<ResponseBodyContentIndex> getIndex(@PathVariable String accessToken) {
        try {
            return ResponseEntity.ok(videoService.getIndex(accessToken));
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new ResponseBodyContentIndex(ex));
        }
    }

    @GetMapping(value = "/base64/{accessToken}/{filename}")
    public ResponseEntity<ResponseBodyContentFileBase64> getFileBase64(@PathVariable String accessToken, @PathVariable String filename) {
        try {
            return ResponseEntity.ok(videoService.getFileBase64(accessToken, filename));
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new ResponseBodyContentFileBase64(ex));
        }
    }
}
