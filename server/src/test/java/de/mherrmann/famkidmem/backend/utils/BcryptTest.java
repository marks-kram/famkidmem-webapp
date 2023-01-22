package de.mherrmann.famkidmem.backend.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BcryptTest {

    private static final String MESSAGE = "1234";

    @Test
    public void shouldCreateValidHash(){
        String hash = Bcrypt.hash(MESSAGE);

        assertThat(hash).matches("^\\$2a\\$\\d{1,2}\\$[A-Za-z0-9/.]{53}$");
    }

    @Test
    public void shouldCheckValid(){
        String hash = Bcrypt.hash(MESSAGE);

        boolean valid = Bcrypt.check(MESSAGE, hash);

        assertThat(valid).isTrue();
    }

    @Test
    public void shouldCheckInalid(){
        String hash = Bcrypt.hash("wrong");

        boolean valid = Bcrypt.check(MESSAGE, hash);

        assertThat(valid).isFalse();
    }
}
