package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.TestData.RANDOM_TESTDATA;
import static de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.TestData.RANDOM_TESTDATA_EXPECTED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl.TranskriptionTransformator;

/**
 * Parametrized test class which tests the {@link TranskriptionTransformator}.
 */
@RunWith(Parameterized.class)
public class TranskriptionTransformatorTest {

    /**
     * Some text with no only latin letters.
     */
    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam";

    /**
     * Expected result for {@link #LOREM_IPSUM}.
     */
    private static final String LORREM_IPSUM_EXPECTED = LOREM_IPSUM.toUpperCase(Locale.GERMANY);

    /**
     * Test data involving german characters.
     */
    private static final String[] UMLAUTE = {"Müller", "Möller", "Häuser", "barfüßig"};

    /**
     * Expected result for {@link #UMLAUTE}.
     */
    private static final String[] UMLAUTE_EXPECTED = {"MUELLER", "MOELLER", "HAEUSER", "BARFUESSIG"};

    /**
     * Test data for the case that that character is not part of the DIN Norm 91379. Default transformation
     * will be used.
     */
    private static final String UNBEKANNT = "ツ";

    /**
     * Expected result for {@link #UNBEKANNT}.
     */
    private static final String UNBEKANNT_EXPECTED = "";

    /**
     * Current test data set by JUnit.
     */
    private final String testData;

    /**
     * Current expected result to {@link #testData} set by JUnit.
     */
    private final String expected;

    /**
     * Composes the different test data to a single collection used by JUnit.
     * @return collection of the test data.
     */
    @Parameterized.Parameters(name = "{index}: testData={0}\nexpected={1}")
    public static Collection<Object[]> data() {
        List<Object[]> testData = new ArrayList<>();
        for (int i = 0; i < RANDOM_TESTDATA.length; i++) {
            testData.add(new String[]{RANDOM_TESTDATA[i], RANDOM_TESTDATA_EXPECTED[i]});
        }

        testData.add(new String[]{LOREM_IPSUM, LORREM_IPSUM_EXPECTED});

        for (int i = 0; i < UMLAUTE.length; i++) {
            testData.add(new String[]{UMLAUTE[i], UMLAUTE_EXPECTED[i]});
        }

        testData.add(new String[]{UNBEKANNT, UNBEKANNT_EXPECTED});

        return testData;
    }

    /**
     * Constructor which sets {@link #testData} and {@link #expected}.
     * @param testData current test data
     * @param expected current expected result
     */
    public TranskriptionTransformatorTest(String testData, String expected){
        this.testData = testData;
        this.expected = expected;

    }

    /**
     * Checks a single transcription from {@link #testData} to {@link #expected}.
     * Will be called multiple times by JUnit.
     */
    @Test
    public void checkTranscription() {
        TranskriptionTransformator transkriptionTransformator = new TranskriptionTransformator();
        transkriptionTransformator.initialisiere(null);

        Assert.assertEquals(transkriptionTransformator.transformiere(testData), expected);
    }
}
