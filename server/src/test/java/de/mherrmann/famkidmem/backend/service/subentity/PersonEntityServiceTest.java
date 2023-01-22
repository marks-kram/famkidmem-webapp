package de.mherrmann.famkidmem.backend.service.subentity;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonEntityServiceTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private PersonEntityService personEntityService;

    @Autowired
    private PersonRepository personRepository;

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldGetPerson(){
        personRepository.save(new Person("person"));
        long persons = personRepository.count();

        Person person = personEntityService.getPerson("person");

        assertThat(person).isNotNull();
        assertThat(personRepository.findAll().iterator().hasNext()).isTrue();
        assertThat(personRepository.findAll().iterator().next().getName()).isEqualTo("person");
        assertThat(personRepository.count()).isEqualTo(persons);
        assertThat(persons).isEqualTo(1);
    }

    @Test
    public void shouldAddPerson(){
        long persons = personRepository.count();

        Person person = personEntityService.getPerson("person");

        assertThat(person).isNotNull();
        assertThat(personRepository.findAll().iterator().hasNext()).isTrue();
        assertThat(personRepository.findAll().iterator().next().getName()).isEqualTo("person");
        assertThat(personRepository.count()).isEqualTo(persons+1);
        assertThat(persons).isEqualTo(0);
    }

    @Test
    public void shouldDelete(){
        Person person = personRepository.save(new Person("person"));
        long persons = personRepository.count();

        personEntityService.delete(person);

        assertThat(person).isNotNull();
        assertThat(personRepository.count()).isEqualTo(0);
        assertThat(persons).isEqualTo(1);
    }
}
