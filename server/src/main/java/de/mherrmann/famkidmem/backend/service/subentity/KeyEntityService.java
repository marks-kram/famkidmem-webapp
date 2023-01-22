package de.mherrmann.famkidmem.backend.service.subentity;

import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyEntityService {

    private final KeyRepository keyRepository;

    @Autowired
    public KeyEntityService(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    public Key addKey(String key, String iv){
        Key keyEntity = new Key(key, iv);
        return keyRepository.save(keyEntity);
    }

    public Key updateKey(String key, String iv, Key oldKey){
        oldKey.setKey(key);
        oldKey.setIv(iv);
        return keyRepository.save(oldKey);
    }

    public void delete(Key key){
        keyRepository.delete(key);
    }
}
