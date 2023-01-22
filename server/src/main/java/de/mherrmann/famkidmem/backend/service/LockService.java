package de.mherrmann.famkidmem.backend.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LockService {

    static final int LOCK_MAX_TRIES = 10;
    static boolean fakeTimeout;

    private Map<String, AttemptData> attemptDataMap = new HashMap<>();
    private long lockDurance = 5*60*1000;

    public synchronized boolean isLocked(String username){
        long now = getCurrentTimeMillis();
        AttemptData attemptData = attemptDataMap.get(username);
        if(attemptData == null){
            return false;
        }
        long diff = now - attemptData.last;
        return attemptData.count >= LOCK_MAX_TRIES && diff < lockDurance;
    }

    synchronized void countAttempt(String username){
        AttemptData attemptData = attemptDataMap.getOrDefault(username, new AttemptData());
        attemptData.count++;
        attemptData.last = getCurrentTimeMillis();
        attemptDataMap.put(username, attemptData);
    }

    synchronized void reset(String username){
        attemptDataMap.remove(username);
    }

    private long getCurrentTimeMillis(){
        return System.currentTimeMillis() + (fakeTimeout ? lockDurance : 0);
    }

    private class AttemptData {
        private int count;
        private long last;
    }
}
