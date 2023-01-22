package de.mherrmann.famkidmem.backend.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(Class type, String designator){
        super(String.format("Entity does not exist. Type: %s; designator: %s", type.getSimpleName(), designator));
    }
}
