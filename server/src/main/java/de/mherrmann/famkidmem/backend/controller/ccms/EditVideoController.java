package de.mherrmann.famkidmem.backend.controller.ccms;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import de.mherrmann.famkidmem.backend.utils.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ccms/edit/video")
public class EditVideoController {

    private final EditVideoService editVideoService;

    @Autowired
    public EditVideoController(EditVideoService editVideoService) {
        this.editVideoService = editVideoService;
    }

    @GetMapping(value = "/get")
    public ResponseEntity<ResponseBodyGetVideos> getVideos(){
        try {
            return ResponseEntity.ok(editVideoService.getVideos());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBodyGetVideos(ex));
        }
    }

    @GetMapping(value = "/get/{title}")
    public ResponseEntity<ResponseBodyGetVideos> getSingleVideo(@PathVariable String title){
        try {
            return ResponseEntity.ok(editVideoService.getSingleVideo(title));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBodyGetVideos(ex));
        }
    }

    @GetMapping(value = "/base64/{filename}")
    public ResponseEntity<ResponseBodyContentFileBase64> getFileBase64(@PathVariable String filename) {
        try {
            return ResponseEntity.ok(editVideoService.getFileBase64(filename));
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new ResponseBodyContentFileBase64(ex));
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ResponseBody> addVideo(@RequestBody RequestBodyAddVideo addVideoRequest){
        try {
            editVideoService.addVideo(addVideoRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully added video: " + addVideoRequest.getTitle()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/update")
    public ResponseEntity<ResponseBody> updateVideo(@RequestBody RequestBodyUpdateVideo updateVideoRequest){
        try {
            editVideoService.updateVideo(updateVideoRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully updated video: " + updateVideoRequest.getTitle()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @DeleteMapping(value = "/delete/{designator}")
    public ResponseEntity<ResponseBody> deleteVideo(@PathVariable String designator){
        designator = ConversionUtil.base64urlToBase64(designator);
        try {
            editVideoService.deleteVideo(designator);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully removed video: " + designator));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }
}
