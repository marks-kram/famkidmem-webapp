package de.mherrmann.famkidmem.backend.service;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LockServiceTest {

    private static final String TEST_USER_NAME = "testUser";

    @Autowired
    private LockService lockService;

    @After
    public void tearDown(){
        lockService.reset(TEST_USER_NAME);
        LockService.fakeTimeout = false;
    }

    @Test
    public void shouldBeLocked(){
        for(int i = 1; i <= LockService.LOCK_MAX_TRIES; i++){
            lockService.countAttempt(TEST_USER_NAME);
        }

        boolean locked = lockService.isLocked(TEST_USER_NAME);

        assertThat(locked).isTrue();
    }

    @Test
    public void shouldBeNotLockedCausedByLessCount(){
        for(int i = 1; i <= LockService.LOCK_MAX_TRIES-1; i++){
            lockService.countAttempt(TEST_USER_NAME);
        }

        boolean locked = lockService.isLocked(TEST_USER_NAME);

        assertThat(locked).isFalse();
    }

    @Test
    public void shouldBeNotLockedCausedByReset(){
        for(int i = 1; i <= LockService.LOCK_MAX_TRIES; i++){
            lockService.countAttempt(TEST_USER_NAME);
        }
        lockService.reset(TEST_USER_NAME);

        boolean locked = lockService.isLocked(TEST_USER_NAME);

        assertThat(locked).isFalse();
    }

    @Test
    public void shouldBeNotLockedCausedByTimeout(){
        for(int i = 1; i <= LockService.LOCK_MAX_TRIES; i++){
            lockService.countAttempt(TEST_USER_NAME);
        }
        LockService.fakeTimeout = true;

        boolean locked = lockService.isLocked(TEST_USER_NAME);

        assertThat(locked).isFalse();
    }

    @Test
    public void shouldBeStillLocked(){
        for(int i = 1; i <= LockService.LOCK_MAX_TRIES; i++){
            lockService.countAttempt(TEST_USER_NAME);
        }
        LockService.fakeTimeout = true;
        lockService.countAttempt(TEST_USER_NAME);

        boolean locked = lockService.isLocked(TEST_USER_NAME);

        assertThat(locked).isTrue();
    }
}
