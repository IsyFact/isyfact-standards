package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.LocalTime;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.AbstractJpaTest;
import de.bund.bva.isyfact.datetime.persistence.jpa.test.TestLocalTimeEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Björn Saxe, msg systems ag
 */
@DatabaseSetup("testLocalTimeSetup.xml")
public class LocalTimeAttributeConverterTest extends AbstractJpaTest {

    @Test
    @ExpectedDatabase(value = "testLocalTimeExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestLocalTimeEntity testEntity = new TestLocalTimeEntity();
        testEntity.setId(2);
        testEntity.setLocalTime(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestLocalTimeEntity testEntity = entityManager.find(TestLocalTimeEntity.class, 1L);

        assertNull(testEntity.getLocalTime());
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestLocalTimeEntity testEntity = entityManager.find(TestLocalTimeEntity.class, 0L);

        assertEquals(LocalTime.of(15, 00, 00), testEntity.getLocalTime());
    }

    @Test
    @ExpectedDatabase(value = "testLocalTimeExpectedWriteLocalTime.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestLocalTimeEntity testEntity = new TestLocalTimeEntity();
        testEntity.setId(2);

        testEntity.setLocalTime(LocalTime.of(15, 23, 45));

        entityManager.persist(testEntity);
    }
}