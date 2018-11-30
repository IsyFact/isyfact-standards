package de.bund.bva.isyfact.persistence.datetime.attributeconverter;

import java.time.OffsetTime;
import java.time.ZoneOffset;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.persistence.datetime.attributeconverter.test.AbstractJpaTest;
import de.bund.bva.isyfact.persistence.datetime.attributeconverter.test.TestOffsetTimeEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**

 */
@DatabaseSetup("testOffsetTimeSetup.xml")
public class OffsetTimeAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testOffsetTimeExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestOffsetTimeEntity testEntity = new TestOffsetTimeEntity();
        testEntity.setId(2);
        testEntity.setOffsetTime(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestOffsetTimeEntity testEntity = entityManager.find(TestOffsetTimeEntity.class, 1L);

        assertNull(testEntity.getOffsetTime());
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestOffsetTimeEntity testEntity = entityManager.find(TestOffsetTimeEntity.class, 0L);

        assertEquals(OffsetTime.of(15, 0, 0, 123456789, ZoneOffset.ofHours(2)), testEntity.getOffsetTime());
    }

    @Test
    @ExpectedDatabase(value = "testOffsetTimeExpectedWriteOffsetTime.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestOffsetTimeEntity testEntity = new TestOffsetTimeEntity();
        testEntity.setId(2);

        testEntity.setOffsetTime(OffsetTime.of(15, 23, 45, 123456789, ZoneOffset.ofHours(3)));

        entityManager.persist(testEntity);
    }
}