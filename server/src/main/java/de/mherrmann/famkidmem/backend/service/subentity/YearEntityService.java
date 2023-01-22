package de.mherrmann.famkidmem.backend.service.subentity;

import de.mherrmann.famkidmem.backend.entity.Year;
import de.mherrmann.famkidmem.backend.repository.YearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class YearEntityService {

    private final YearRepository yearRepository;

    @Autowired
    public YearEntityService(YearRepository yearRepository) {
        this.yearRepository = yearRepository;
    }

    public Year getYear(int value){
        Optional<Year> yearOptional = yearRepository.findByValue(value);
        return yearOptional.orElseGet(() -> addYear(value));
    }

    public void delete(Year year){
        yearRepository.delete(year);
    }

    private Year addYear(int value){
        return yearRepository.save(new Year(value));
    }
}
