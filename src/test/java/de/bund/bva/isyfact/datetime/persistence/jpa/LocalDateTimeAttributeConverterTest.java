package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.LocalDateTime;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestLocalDateTimeEntity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Björn Saxe, msg systems ag
 */
@DatabaseSetup("testLocalDateTimeSetup.xml")
public class LocalDateTimeAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testLocalDateTimeExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestLocalDateTimeEntity testEntity = new TestLocalDateTimeEntity();
        testEntity.setId(2);

        testEntity.setLocalDateTime(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestLocalDateTimeEntity testEntity = entityManager.find(TestLocalDateTimeEntity.class, 1L);

        assertNull(testEntity.getLocalDateTime());
    }

    @Test
    @ExpectedDatabase(value = "testLocalDateTimeExpectedWriteLocalDateTime.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestLocalDateTimeEntity testEntity = new TestLocalDateTimeEntity();
        testEntity.setId(2);
        testEntity.setLocalDateTime(LocalDateTime.of(2017, 1, 1, 15, 23, 45, 123456789));

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestLocalDateTimeEntity testEntity = entityManager.find(TestLocalDateTimeEntity.class, 0L);

        assertEquals(LocalDateTime.of(2017, 8, 16, 15, 0, 0, 123456789), testEntity.getLocalDateTime());
    }

}