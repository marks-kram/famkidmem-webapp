package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.exception.LockException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import de.mherrmann.famkidmem.backend.entity.UserSession;
import de.mherrmann.famkidmem.backend.exception.LoginException;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final LockService lockService;

    private static final long SESSION_TIME_TO_LIVE = 24*60*60*1000; //One day

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, SessionRepository sessionRepository, LockService lockService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.lockService = lockService;
    }

    public ResponseBodyLogin login(String username, String loginHash, boolean permanent) throws LoginException, LockException {
        if(lockService.isLocked(username)){
            throw new LockException();
        }
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if(!userOptional.isPresent()){
            LOGGER.error("Could not login user. Invalid username {}", username);
            lockService.countAttempt(username);
            throw new LoginException();
        }
        UserEntity user = userOptional.get();
        String loginHashHash = user.getLoginHashHash();
        if(!Bcrypt.check(loginHash, loginHashHash)){
            LOGGER.error("Could not login user. Invalid loginHash");
            lockService.countAttempt(username);
            throw new LoginException();
        }
        String accessToken = UUID.randomUUID().toString();
        addUserSession(user, accessToken, permanent);
        createNewHash(user, loginHash);
        LOGGER.info("Successfully logged in {} with accessToken {}", user.getUsername(), accessToken);
        lockService.reset(username);
        return new ResponseBodyLogin(accessToken, user.getPasswordKeySalt());
    }

    public void logout(String accessToken, boolean global) throws SecurityException {
        UserSession session = getUserSession(accessToken, "logout user");
        if(global){
            UserEntity user = session.getUserEntity();
            LOGGER.info("Successfully logged out {} from all sessions", session.getUserEntity().getUsername());
            sessionRepository.deleteAllByUserEntity(user);
        } else {
            sessionRepository.delete(session);
            LOGGER.info("Successfully logged out {} from accessToken {}", session.getUserEntity().getUsername(), accessToken);
        }
    }

    public void changeUsername(String accessToken, String newUsername, String newLoginHash) throws SecurityException {
        UserEntity user = getUser(accessToken, "change username and/or password");
        changeUsernameAndOrPassword(user, newUsername, newLoginHash, user.getPasswordKeySalt(), user.getMasterKey());
    }

    public void changePassword(String accessToken, String newLoginHash, String newPasswordKeySalt, String newMasterKey) throws SecurityException {
        UserEntity user = getUser(accessToken, "change username and/or password");
        user.setReset(false);
        changeUsernameAndOrPassword(user, user.getUsername(), newLoginHash, newPasswordKeySalt, newMasterKey);
    }

    public void changeUsernameAndPassword(String accessToken, String newUsername, String newLoginHash, String newPasswordKeySalt, String newMasterKey) throws SecurityException {
        UserEntity user = getUser(accessToken, "change username and/or password");
        user.setInit(false);
        changeUsernameAndOrPassword(user, newUsername, newLoginHash, newPasswordKeySalt, newMasterKey);
    }

    public String getMasterKey(String accessToken) throws SecurityException {
        UserEntity user = getUser(accessToken, "get masterKey");
        LOGGER.info("Successfully got masterKey for user {}", user.getUsername());
        return user.getMasterKey();
    }

    public UserEntity getUser(String accessToken, String action) throws SecurityException {
        return getUserSession(accessToken, action).getUserEntity();
    }

    private void changeUsernameAndOrPassword(UserEntity user, String newUsername, String newLoginHash,
                                          String newPasswordKeySalt, String newMasterKey) throws SecurityException {

        String oldUsername = user.getUsername();
        if(newUsername.matches("[^a-zA-Z0-9._=\\-]")){
            LOGGER.error("Could not change username and/or password. Invalid username {}", newUsername);
            throw new SecurityException("change username and/or password (invalid username)");
        }
        user.setUsername(newUsername);
        if(newLoginHash != null){
            String newLoginHashHash = Bcrypt.hash(newLoginHash);
            user.setLoginHashHash(newLoginHashHash);
        }
        user.setMasterKey(newMasterKey);
        user.setPasswordKeySalt(newPasswordKeySalt);
        user.setReset(false);
        userRepository.save(user);
        LOGGER.info("Successfully changed username and/or password. {} -> {}", oldUsername, user.getUsername());
    }

    private UserSession getUserSession(String accessToken, String action) throws SecurityException {
        Optional<UserSession> sessionOptional = sessionRepository.findByAccessToken(accessToken);
        if(!sessionOptional.isPresent()){
            LOGGER.error("Could not {}. Invalid accessToken {}", action, accessToken);
            throw new SecurityException(action);
        }
        return sessionOptional.get();
    }

    private void createNewHash(UserEntity user, String loginHash){
        String newHash = Bcrypt.hash(loginHash);
        user.setLoginHashHash(newHash);
        userRepository.save(user);
    }

    private void addUserSession(UserEntity user, String accessToken, boolean permanent){
        deleteOldSessions(user);
        UserSession session = new UserSession(user, accessToken, permanent);
        sessionRepository.save(session);
    }

    private void deleteOldSessions(UserEntity user){
        long threshold = System.currentTimeMillis() - SESSION_TIME_TO_LIVE;
        sessionRepository.deleteAllByPermanentIsFalseAndCreatedBefore(new Timestamp(threshold));
        sessionRepository.deleteAllByPermanentIsFalseAndUserEntity(user);
    }
}
