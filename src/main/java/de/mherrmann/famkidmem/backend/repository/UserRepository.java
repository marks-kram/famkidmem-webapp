package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findById(String id);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    Iterable<UserEntity> findAllByOrderById();
}
