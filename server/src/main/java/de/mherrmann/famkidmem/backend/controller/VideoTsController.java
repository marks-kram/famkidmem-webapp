package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.service.VideoService;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class VideoTsController {

    private final VideoService videoService;
    private final EditVideoService editVideoService;

    @Autowired
    public VideoTsController(VideoService videoService, EditVideoService editVideoService) {
        this.videoService = videoService;
        this.editVideoService = editVideoService;
    }



    @ResponseBody
    @GetMapping(value = "/api/ts/{accessToken}/{filename}")
    public ResponseEntity<ByteArrayResource> getTsAuthorizedByLogin(@PathVariable String accessToken, @PathVariable String filename) {
        try {
            return videoService.getTsFile(accessToken, filename);
        } catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @ResponseBody
    @GetMapping(value = "/ccms/edit/video/ts/{filename}")
    public ResponseEntity<ByteArrayResource> getTsAuthorizedByApiKey(@PathVariable String filename) {
        try {
            return editVideoService.getTsFile(filename);
        } catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }
}
