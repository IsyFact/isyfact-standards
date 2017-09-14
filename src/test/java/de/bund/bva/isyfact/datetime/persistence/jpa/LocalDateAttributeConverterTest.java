package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.LocalDate;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestLocalDateEntity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Björn Saxe, msg systems ag
 */
@DatabaseSetup("testLocalDateSetup.xml")
public class LocalDateAttributeConverterTest extends AbstractJpaTest {

    @Test
    public void convertToEntityAttributeNull() {
        TestLocalDateEntity testEntity = entityManager.find(TestLocalDateEntity.class, 1L);

        assertNull(testEntity.getLocalDate());
    }

    @Test
    @ExpectedDatabase(value = "testLocalDateExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() {
        TestLocalDateEntity testEntity = new TestLocalDateEntity();

        testEntity.setId(2);
        testEntity.setLocalDate(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttribute() {
        TestLocalDateEntity testEntity = entityManager.find(TestLocalDateEntity.class, 0L);

        assertEquals(LocalDate.of(2017, 8, 16), testEntity.getLocalDate());
    }

    @Test
    @ExpectedDatabase(value = "testLocalDateExpectedWriteLocalDate.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() {
        TestLocalDateEntity testEntity = new TestLocalDateEntity();
        testEntity.setId(2);

        testEntity.setLocalDate(LocalDate.of(2017, 1, 1));

        entityManager.persist(testEntity);
    }
}