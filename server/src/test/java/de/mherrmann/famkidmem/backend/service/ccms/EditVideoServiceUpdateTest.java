package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.EntityActionException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.repository.YearRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EditVideoServiceUpdateTest {

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private YearRepository yearRepository;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setup() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldUpdateVideo() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        Exception exception = null;

        try {
            editVideoService.updateVideo(updateVideoRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(videoRepository.existsByTitle(updateVideoRequest.getTitle())).isTrue();
        assertToBe(updateVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByEntityNotFound() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setDesignator("invalid");

        shouldFailUpdateVideo(updateVideoRequest, EntityNotFoundException.class);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingTitle() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setTitle("");

        shouldFailUpdateVideo(updateVideoRequest, EntityActionException.class);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingKey() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setKey("");

        shouldFailUpdateVideo(updateVideoRequest, EntityActionException.class);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingIv() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setIv("");

        shouldFailUpdateVideo(updateVideoRequest, EntityActionException.class);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailKey() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setThumbnailKey("");

        shouldFailUpdateVideo(updateVideoRequest, EntityActionException.class);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailIv() {
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        updateVideoRequest.setThumbnailIv("");

        shouldFailUpdateVideo(updateVideoRequest, EntityActionException.class);
    }

    @Test
    public void shouldFailAddVideoCausedByEponymousVideoExists() throws Exception {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        RequestBodyUpdateVideo updateVideoRequest = testUtils.createUpdateVideoRequest();
        addVideoRequest.setTitle("eponymous");
        updateVideoRequest.setTitle("eponymous");
        try {
            editVideoService.addVideo(addVideoRequest);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        shouldFailUpdateVideo(updateVideoRequest, EntityActionException.class);
    }

    private void assertToBe(RequestBodyUpdateVideo updateVideoRequest){
        Video video = videoRepository.findByTitle(updateVideoRequest.getTitle()).get();
        assertThat(video.getTitle()).isEqualTo(updateVideoRequest.getTitle());
        assertThat(video.getDescription()).isEqualTo(updateVideoRequest.getDescription());
        assertThat(video.getKey().getKey()).isEqualTo(updateVideoRequest.getKey());
        assertThat(video.getKey().getIv()).isEqualTo(updateVideoRequest.getIv());
        assertThat(video.getThumbnail().getKey().getKey()).isEqualTo(updateVideoRequest.getThumbnailKey());
        assertThat(video.getThumbnail().getKey().getIv()).isEqualTo(updateVideoRequest.getThumbnailIv());
        assertThat(video.getPersons()).isNotEmpty();
        assertThat(video.getYears()).isNotEmpty();
        assertThat(video.getPersons().get(0).getName()).isEqualTo(updateVideoRequest.getPersons().get(0));
        assertThat(video.getPersons().get(1).getName()).isEqualTo(updateVideoRequest.getPersons().get(1));
        assertThat(video.getYears().get(0).getValue()).isEqualTo(updateVideoRequest.getYears().get(0));
        assertThat(video.getYears().get(1).getValue()).isEqualTo(updateVideoRequest.getYears().get(1));
        assertThat(video.isRecordedInCologne()).isEqualTo(updateVideoRequest.isRecordedInCologne());
        assertThat(video.isRecordedInGardelegen()).isEqualTo(updateVideoRequest.isRecordedInGardelegen());
        assertThat(personRepository.findByName("person1").isPresent()).isFalse();
        assertThat(yearRepository.findByValue(1995).isPresent()).isFalse();
        assertThat(video.getTimestamp().getTime()).isEqualTo(updateVideoRequest.getTimestamp());
        assertThat(video.getShowDateValues()).isEqualTo(updateVideoRequest.getShowDateValues());
    }

    private void shouldFailUpdateVideo(RequestBodyUpdateVideo updateVideoRequest, Class exceptionClass){
        Exception exception = null;
        long videos = videoRepository.count();

        try {
            editVideoService.updateVideo(updateVideoRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
        assertThat(videoRepository.count()).isEqualTo(videos);
    }
}

