package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.entity.Year;
import de.mherrmann.famkidmem.backend.exception.FileNotFoundException;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.repository.YearRepository;
import de.mherrmann.famkidmem.backend.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final PersonRepository personRepository;
    private final YearRepository yearRepository;
    private final UserService userService;
    private final FileUtil fileUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    public VideoService(
            VideoRepository videoRepository, PersonRepository personRepository, YearRepository yearRepository,
            UserService userService, FileUtil fileUtil) {
        this.videoRepository = videoRepository;
        this.personRepository = personRepository;
        this.yearRepository = yearRepository;
        this.userService = userService;
        this.fileUtil = fileUtil;
    }

    public ResponseBodyContentIndex getIndex(String accessToken) throws SecurityException {
        UserEntity user = userService.getUser(accessToken, "get video index");
        LOGGER.info("Successfully got video index. AccessToken: {}", accessToken);
        return new ResponseBodyContentIndex(user.getMasterKey(), getVideos(user), getPersons(), getYears());
    }

    public ResponseBodyContentFileBase64 getFileBase64(String accessToken, String filename) throws SecurityException, FileNotFoundException, IOException {
        UserEntity user = userService.getUser(accessToken, "get file base64");
        LOGGER.info("Successfully got file base64 encoded. AccessToken: {}", accessToken);
        return new ResponseBodyContentFileBase64(user.getMasterKey(), fileUtil.getBase64EncodedContent(filename));
    }

    public ResponseEntity<ByteArrayResource> getTsFile(String accessToken, String filename) throws SecurityException, FileNotFoundException, IOException {
        userService.getUser(accessToken, "get ts file");
        ResponseEntity<ByteArrayResource> responseEntity = fileUtil.getContentResponseEntity(filename);
        LOGGER.info("Successfully got ts file. AccessToken: {}", accessToken);
        return responseEntity;
    }

    private List<Video> getVideos(UserEntity user){
        List<Video> videos = new ArrayList<>();
        Iterable<Video> videoIterable;
        if(user.isPermission2()){
            videoIterable = videoRepository.findAllByOrderByTimestamp();
        } else {
            videoIterable = videoRepository.findAllByPermission2IsFalseOrderByTimestamp();
        }
        videoIterable.forEach(videos::add);
        return videos;
    }

    private List<String> getPersons(){
        List<String> persons = new ArrayList<>();
        Iterable<Person> personsIterable = personRepository.findAllByOrderByNameAsc();
        personsIterable.forEach(e->persons.add(e.getName()));
        return persons;
    }

    private List<Integer> getYears(){
        List<Integer> years = new ArrayList<>();
        Iterable<Year> yearsIterable = yearRepository.findAllByOrderByValueAsc();
        yearsIterable.forEach(e->years.add(e.getValue()));
        return years;
    }
}
