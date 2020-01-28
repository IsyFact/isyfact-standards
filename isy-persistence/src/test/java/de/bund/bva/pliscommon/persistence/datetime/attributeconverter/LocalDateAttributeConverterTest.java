package de.bund.bva.pliscommon.persistence.datetime.attributeconverter;

import java.time.LocalDate;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test.AbstractJpaTest;
import de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test.TestLocalDateEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**

 */
@Deprecated
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