package de.bund.bva.pliscommon.konfiguration.common.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Testet die Klasse {@link DateinamenSortiererUndFilterer}}
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DateinamenSortiererUndFiltererTest {

    private DateinamenSortiererUndFilterer dateinamenSortiererUndFilterer;

    @Mock
    PropertyDatei propertyDateiA;

    @Mock
    PropertyDatei propertyDateiB;

    @Mock
    PropertyDatei propertyDateiC;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testSortiereUndFiltereDateinamenAusStringSet() {

        // Arrange
        this.dateinamenSortiererUndFilterer = new DateinamenSortiererUndFilterer();

        Set<String> propertyDateienList = new HashSet<>();
        ArrayList<String> erwartetePropertyDateienList = new ArrayList<>();

        String abcProperties = "abc.properties";
        String bcdProperties = "bcd.properties";
        String cdeProperies = "cde.properies";

        erwartetePropertyDateienList.add(abcProperties);
        erwartetePropertyDateienList.add(bcdProperties);
        propertyDateienList.add(bcdProperties);
        propertyDateienList.add(abcProperties);
        propertyDateienList.add(cdeProperies);

        // Act
        List<String> aktuellePropertyDateienList =
            this.dateinamenSortiererUndFilterer.sortiereUndFiltereDateinamenAusStringSet(propertyDateienList);

        // Assert
        this.softly.assertThat(aktuellePropertyDateienList).isEqualTo(erwartetePropertyDateienList);
    }

    @Test
    public void testSortiereUndFiltereDateinamenAusPropertyDateiList() {

        // Arrange
        MockitoAnnotations.initMocks(this);

        this.dateinamenSortiererUndFilterer = new DateinamenSortiererUndFilterer();

        List<PropertyDatei> propertyDateienList = new ArrayList<>();
        ArrayList<PropertyDatei> erwartetePropertyDateienList = new ArrayList<>();

        when(this.propertyDateiA.getDateiname()).thenReturn("abc.properties");
        when(this.propertyDateiB.getDateiname()).thenReturn("bcd.properties");
        when(this.propertyDateiC.getDateiname()).thenReturn("cde.properies");

        erwartetePropertyDateienList.add(this.propertyDateiA);
        erwartetePropertyDateienList.add(this.propertyDateiB);
        propertyDateienList.add(this.propertyDateiB);
        propertyDateienList.add(this.propertyDateiA);
        propertyDateienList.add(this.propertyDateiC);

        // Act
        List<PropertyDatei> aktuellePropertyDateienList = this.dateinamenSortiererUndFilterer
            .sortiereUndFiltereDateinamenAusPropertyDateiList(propertyDateienList);

        // Assert
        this.softly.assertThat(aktuellePropertyDateienList).isEqualTo(erwartetePropertyDateienList);
    }
}
