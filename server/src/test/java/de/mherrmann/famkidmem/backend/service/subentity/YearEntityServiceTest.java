package de.mherrmann.famkidmem.backend.service.subentity;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.entity.Year;
import de.mherrmann.famkidmem.backend.repository.YearRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YearEntityServiceTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private YearEntityService yearEntityService;

    @Autowired
    private YearRepository yearRepository;

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldGetYear(){
        yearRepository.save(new Year(1994));
        long years = yearRepository.count();

        Year year = yearEntityService.getYear(1994);

        assertThat(year).isNotNull();
        assertThat(yearRepository.findAll().iterator().hasNext()).isTrue();
        assertThat(yearRepository.findAll().iterator().next().getValue()).isEqualTo(1994);
        assertThat(yearRepository.count()).isEqualTo(years);
        assertThat(years).isEqualTo(1);
    }

    @Test
    public void shouldAddYear(){
        long years = yearRepository.count();

        Year year = yearEntityService.getYear(1995);

        assertThat(year).isNotNull();
        assertThat(yearRepository.findAll().iterator().hasNext()).isTrue();
        assertThat(yearRepository.findAll().iterator().next().getValue()).isEqualTo(1995);
        assertThat(yearRepository.count()).isEqualTo(years+1);
        assertThat(years).isEqualTo(0);
    }

    @Test
    public void shouldDelete(){
        Year year = yearRepository.save(new Year(1998));
        long years = yearRepository.count();

        yearEntityService.delete(year);

        assertThat(year).isNotNull();
        assertThat(yearRepository.count()).isEqualTo(0);
        assertThat(years).isEqualTo(1);
    }
}
