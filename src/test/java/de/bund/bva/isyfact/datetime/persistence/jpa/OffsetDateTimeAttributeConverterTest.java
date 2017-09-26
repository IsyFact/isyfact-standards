package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestOffsetDateTimeEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**

 */
@DatabaseSetup("testOffsetDateTimeSetup.xml")
public class OffsetDateTimeAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testOffsetDateTimeExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestOffsetDateTimeEntity testEntity = new TestOffsetDateTimeEntity();
        testEntity.setId(2);
        testEntity.setOffsetDateTime(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestOffsetDateTimeEntity testEntity = entityManager.find(TestOffsetDateTimeEntity.class, 1L);

        assertNull(testEntity.getOffsetDateTime());
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestOffsetDateTimeEntity testEntity = entityManager.find(TestOffsetDateTimeEntity.class, 0L);

        assertEquals(OffsetDateTime.of(2017, 8, 1, 15, 0, 0, 123456789, ZoneOffset.ofHours(2)),
            testEntity.getOffsetDateTime());
    }

    @Test
    @ExpectedDatabase(value = "testOffsetDateTimeExpectedWriteOffsetDateTime.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestOffsetDateTimeEntity testEntity = new TestOffsetDateTimeEntity();
        testEntity.setId(2);

        testEntity
            .setOffsetDateTime(OffsetDateTime.of(2017, 8, 1, 15, 23, 45, 123456789, ZoneOffset.ofHours(3)));

        entityManager.persist(testEntity);
    }
}