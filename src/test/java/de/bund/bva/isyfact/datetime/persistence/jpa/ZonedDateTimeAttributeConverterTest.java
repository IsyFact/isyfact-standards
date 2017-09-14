package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestOffsetDateTimeEntity;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestZonedDateTimeEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Björn Saxe, msg systems ag
 */
@DatabaseSetup("testZonedDateTimeSetup.xml")
public class ZonedDateTimeAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testZonedDateTimeExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestZonedDateTimeEntity testEntity = new TestZonedDateTimeEntity();
        testEntity.setId(2);
        testEntity.setZonedDateTime(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestZonedDateTimeEntity testEntity = entityManager.find(TestZonedDateTimeEntity.class, 1L);

        assertNull(testEntity.getZonedDateTime());
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestZonedDateTimeEntity testEntity = entityManager.find(TestZonedDateTimeEntity.class, 0L);

        assertEquals(ZonedDateTime.of(2017, 8, 1, 15, 0, 0, 123456789, ZoneId.of("Europe/Berlin")),
            testEntity.getZonedDateTime());
    }

    @Test
    @ExpectedDatabase(value = "testZonedDateTimeExpectedWriteZonedDateTime.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestZonedDateTimeEntity testEntity = new TestZonedDateTimeEntity();
        testEntity.setId(2);

        testEntity.setZonedDateTime(
            ZonedDateTime.of(2017, 8, 1, 15, 23, 45, 123456789, ZoneId.of("Europe/Moscow")));

        entityManager.persist(testEntity);
    }
}