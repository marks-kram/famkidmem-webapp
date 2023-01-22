package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Year;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YearRepository extends CrudRepository<Year, String> {
    Optional<Year> findByValue(int value);
    Iterable<Year> findAllByOrderByValueAsc();
}
