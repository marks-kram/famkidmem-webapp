package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.entity.Year;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends CrudRepository<Video, String> {
    boolean existsByTitle(String title);
    boolean existsByPersonsContains(Person person);
    boolean existsByYearsContains(Year year);
    Optional<Video> findByTitle(String title);
    Iterable<Video> findAllByOrderByTimestamp();
    Iterable<Video> findAllByPermission2IsFalseOrderByTimestamp();
}
