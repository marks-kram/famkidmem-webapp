package de.mherrmann.famkidmem.backend.utils;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.exception.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public String getBase64EncodedContent(String filename) throws FileNotFoundException, IOException {
        File file = new File(Application.filesDir + filename.replaceAll("\\.+", "."));
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte fileData[] = new byte[(int) file.length()];
            imageInFile.read(fileData);
            return Base64.getEncoder().encodeToString(fileData);
        } catch (java.io.FileNotFoundException e) {
            LOGGER.error("Could not get base64 of file {}. File does not exist or is a directory.", filename);
            throw new FileNotFoundException(filename);
        } catch (IOException ioe) {
            LOGGER.error("Could not get base64 of file {}. There was an error reading the file.", filename);
            throw new IOException("There was an error reading the file: " + filename);
        }
    }

    public ResponseEntity<ByteArrayResource> getContentResponseEntity(String filename) throws FileNotFoundException, IOException {
        File file = new File(Application.filesDir + filename.replaceAll("\\.+", "."));
        if(!file.exists() || file.isDirectory()){
            LOGGER.error("Could not get ts file contents. File: {}. File does not exist or is a directory.", filename);
            throw new FileNotFoundException(filename);
        }
        try {
            long length = file.length();
            String mimeType = Files.probeContentType(file.toPath());
            byte[] byteArray = Files.readAllBytes(file.toPath());
            return ResponseEntity
                    .ok()
                    .contentLength(length)
                    .contentType(filename.endsWith(".ts") ? MediaType.parseMediaType("video/vnd.dlna.mpeg-tts") : MediaType.parseMediaType(mimeType))
                    .body(new ByteArrayResource(byteArray));
        } catch(IOException ex){
            LOGGER.error("Could not get ts file contents. File: {}. There was an error reading the file.", filename);
            throw new IOException("There was an error reading the file: " + filename);
        }
    }
}
