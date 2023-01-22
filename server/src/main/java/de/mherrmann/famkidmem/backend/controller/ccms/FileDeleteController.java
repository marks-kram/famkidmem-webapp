package de.mherrmann.famkidmem.backend.controller.ccms;

import de.mherrmann.famkidmem.backend.service.ccms.FileDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FileDeleteController {

    private final FileDeleteService fileDeleteService;

    @Autowired
    public FileDeleteController(FileDeleteService fileDeleteService) {
        this.fileDeleteService = fileDeleteService;
    }

    @DeleteMapping("/ccms/delete/{fileName}")
    public ResponseEntity<String> handleFileDeletion(@PathVariable String fileName){
        try {
            fileDeleteService.deleteFile(fileName);
            return ResponseEntity.ok("ok");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body("error: " + ex.getMessage());
        }

    }

}
