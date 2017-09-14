package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.Duration;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestDurationEntity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Björn Saxe, msg systems ag
 */
@DatabaseSetup("testDurationSetup.xml")
public class DurationAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testDurationExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestDurationEntity testEntity = new TestDurationEntity();
        testEntity.setId(2);
        testEntity.setDuration(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestDurationEntity entity = entityManager.find(TestDurationEntity.class, 1L);

        assertNull(entity.getDuration());
    }

    @Test
    @ExpectedDatabase(value = "testDurationExpectedWriteDuration.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestDurationEntity testEntity = new TestDurationEntity();
        testEntity.setId(2);
        testEntity.setDuration(Duration.ofNanos(9223372036854775807L));

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestDurationEntity testEntity = entityManager.find(TestDurationEntity.class, 0L);

        assertEquals(Duration.ofNanos(10123456789L), testEntity.getDuration());
    }

}