package de.mherrmann.famkidmem.backend.service.subentity;

import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonEntityService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonEntityService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person getPerson(String name){
        Optional<Person> personOptional = personRepository.findByName(name);
        return personOptional.orElseGet(() -> addPerson(name));
    }

    public void delete(Person person){
        personRepository.delete(person);
    }

    private Person addPerson(String name){
        return personRepository.save(new Person(name));
    }
}
