package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<FileEntity, String> {

}
