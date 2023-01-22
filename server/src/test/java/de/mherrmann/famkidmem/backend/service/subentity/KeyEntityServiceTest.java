package de.mherrmann.famkidmem.backend.service.subentity;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.entity.Key;
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
public class KeyEntityServiceTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private KeyEntityService keyEntityService;

    @Autowired
    private KeyRepository keyRepository;

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldAddKey(){

        Key key = keyEntityService.addKey("key", "iv");

        assertThat(key).isNotNull();
        assertThat(keyRepository.findAll().iterator().hasNext()).isTrue();
        assertThat(keyRepository.findAll().iterator().next().getKey()).isEqualTo("key");
        assertThat(keyRepository.findAll().iterator().next().getIv()).isEqualTo("iv");
    }

    @Test
    public void shouldUpdateKey(){
        Key oldKey = keyRepository.save(new Key("key", "iv"));

        Key key = keyEntityService.updateKey("newKey", "newIv", oldKey);

        assertThat(key).isNotNull();
        assertThat(keyRepository.count()).isEqualTo(1);
        assertThat(keyRepository.findAll().iterator().next().getKey()).isEqualTo("newKey");
        assertThat(keyRepository.findAll().iterator().next().getIv()).isEqualTo("newIv");
    }

    @Test
    public void shouldDelete(){
        Key key = keyRepository.save(new Key("key", "iv"));
        long keys = keyRepository.count();

        keyEntityService.delete(key);

        assertThat(key).isNotNull();
        assertThat(keyRepository.count()).isEqualTo(0);
        assertThat(keys).isEqualTo(1);
    }
}
