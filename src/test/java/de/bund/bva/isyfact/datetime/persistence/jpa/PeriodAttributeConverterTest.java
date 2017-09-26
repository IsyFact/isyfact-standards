package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.Period;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestPeriodEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**

 */
@DatabaseSetup("testPeriodSetup.xml")
public class PeriodAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testPeriodExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestPeriodEntity testEntity = new TestPeriodEntity();
        testEntity.setId(2);
        testEntity.setPeriod(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestPeriodEntity testEntity = entityManager.find(TestPeriodEntity.class, 1L);

        assertNull(testEntity.getPeriod());
    }

    @Test
    @ExpectedDatabase(value = "testPeriodExpectedWritePeriod.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestPeriodEntity testEntity = new TestPeriodEntity();
        testEntity.setId(2);
        testEntity.setPeriod(Period.of(3, 4, 5));

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestPeriodEntity testEntity = entityManager.find(TestPeriodEntity.class, 0L);

        assertEquals(Period.of(10, 20, 30), testEntity.getPeriod());
    }
}