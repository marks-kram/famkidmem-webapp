package de.mherrmann.famkidmem.backend.service.subentity;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.entity.FileEntity;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.repository.FileRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileEntityServiceTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private FileEntityService fileEntityService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private KeyRepository keyRepository;

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddFile(){
        Key key = keyRepository.save(new Key("key", "iv"));

        FileEntity file = fileEntityService.addFile(key, "filename");

        assertThat(file).isNotNull();
        assertThat(fileRepository.findAll().iterator().hasNext()).isTrue();
        assertThat(fileRepository.findAll().iterator().next().getKey().getKey()).isEqualTo(key.getKey());
        assertThat(fileRepository.findAll().iterator().next().getFilename()).isEqualTo("filename");
    }

    @Test
    public void shouldDelete(){
        Key key = keyRepository.save(new Key("key", "iv"));
        FileEntity file = fileRepository.save(new FileEntity(key, "filename"));
        long files = fileRepository.count();

        fileEntityService.delete(file);

        assertThat(file).isNotNull();
        assertThat(fileRepository.count()).isEqualTo(0);
        assertThat(files).isEqualTo(1);
    }
}
