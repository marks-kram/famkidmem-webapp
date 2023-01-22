package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.exception.FileDeleteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileDeleteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDeleteService.class);

    public void deleteFile(String fileName) throws FileDeleteException{
        checkIfExists(fileName);
        delete(fileName);
    }

    private void checkIfExists(String fileName) throws FileDeleteException {
        if(!new File(Application.filesDir + fileName).exists()){
            LOGGER.error("Can not delete file {}. File does not exist.", fileName);
            throw new FileDeleteException("Can not delete non-existing file.");
        }
    }

    private void delete(String fileName) throws FileDeleteException {
        boolean removed = new File(Application.filesDir + fileName).delete();
        if(!removed) {
            LOGGER.error("Can not delete file {}. I/O Error.", fileName);
            throw new FileDeleteException("Can not delete file. I/O Error");
        }
    }
}
