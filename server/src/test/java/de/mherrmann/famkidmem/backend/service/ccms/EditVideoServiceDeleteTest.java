package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.*;
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
public class EditVideoServiceDeleteTest {

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private YearRepository yearRepository;

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setup() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        editVideoService.addVideo(testUtils.createAddAnotherVideoRequest());
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldDeleteVideo() {
        Exception exception = null;
        Video removed = videoRepository.findByTitle("title/").get();
        Video remained = videoRepository.findByTitle("video2").get();

        try {
            editVideoService.deleteVideo("title/");
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertRemoved(removed, remained);
    }

    @Test
    public void shouldFailDeleteVideoCausedByEntityNotFound() {
        Exception exception = null;

        try {
            editVideoService.deleteVideo("invalid");
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(EntityNotFoundException.class);
        assertThat(videoRepository.existsByTitle("title/")).isTrue();
        assertThat(videoRepository.existsByTitle("video2")).isTrue();
    }

    private void assertRemoved(Video removed, Video remained){
        assertThat(videoRepository.existsById(removed.getId())).isFalse();
        assertThat(videoRepository.existsById(remained.getId())).isTrue();
        assertThat(personRepository.existsById(removed.getPersons().get(0).getId())).isFalse();
        assertThat(personRepository.existsById(removed.getPersons().get(1).getId())).isFalse();
        assertThat(personRepository.existsById(remained.getPersons().get(0).getId())).isTrue();
        assertThat(personRepository.existsById(remained.getPersons().get(1).getId())).isTrue();
        assertThat(yearRepository.existsById(removed.getYears().get(0).getId())).isFalse();
        assertThat(yearRepository.existsById(removed.getYears().get(1).getId())).isFalse();
        assertThat(yearRepository.existsById(remained.getYears().get(0).getId())).isTrue();
        assertThat(yearRepository.existsById(remained.getYears().get(1).getId())).isTrue();
        assertThat(keyRepository.existsById(removed.getKey().getId())).isFalse();
        assertThat(keyRepository.existsById(removed.getM3u8().getKey().getId())).isFalse();
        assertThat(keyRepository.existsById(removed.getThumbnail().getKey().getId())).isFalse();
        assertThat(keyRepository.existsById(remained.getKey().getId())).isTrue();
        assertThat(keyRepository.existsById(remained.getM3u8().getKey().getId())).isTrue();
        assertThat(keyRepository.existsById(remained.getThumbnail().getKey().getId())).isTrue();
        assertThat(fileRepository.existsById(removed.getM3u8().getId())).isFalse();
        assertThat(fileRepository.existsById(removed.getThumbnail().getId())).isFalse();
        assertThat(fileRepository.existsById(remained.getM3u8().getId())).isTrue();
        assertThat(fileRepository.existsById(remained.getThumbnail().getId())).isTrue();
    }
}
