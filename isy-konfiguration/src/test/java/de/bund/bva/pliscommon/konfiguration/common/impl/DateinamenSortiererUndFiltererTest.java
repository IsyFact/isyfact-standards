package de.bund.bva.pliscommon.konfiguration.common.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Testet die Klasse {@link DateinamenSortierer}}
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DateinamenSortiererUndFiltererTest {

    private DateinamenSortierer dateinamenSortierer;

    @Mock
    PropertyDatei propertyDateiA;

    @Mock
    PropertyDatei propertyDateiB;

    @Mock
    PropertyDatei propertyDateiC;

    @Test
    public void testSortiereDateinamenAusStringSet() {

        // Arrange
        this.dateinamenSortierer = new DateinamenSortierer();

        Set<String> propertyDateienList = new HashSet<>();
        ArrayList<String> erwartetePropertyDateienList = new ArrayList<>();

        String abcProperties = "abc.properties";
        String bcdProperties = "bcd.properties";

        erwartetePropertyDateienList.add(abcProperties);
        erwartetePropertyDateienList.add(bcdProperties);
        propertyDateienList.add(bcdProperties);
        propertyDateienList.add(abcProperties);

        // Act
        List<String> aktuellePropertyDateienList =
            this.dateinamenSortierer.sortiereDateinamenAusStringSet(propertyDateienList);

        // Assert
        assertEquals(aktuellePropertyDateienList, erwartetePropertyDateienList);
    }

    @Test
    public void testSortiereDateinamenAusPropertyDateiList() {

        // Arrange
        MockitoAnnotations.initMocks(this);

        this.dateinamenSortierer = new DateinamenSortierer();

        List<PropertyDatei> propertyDateienList = new ArrayList<>();
        ArrayList<PropertyDatei> erwartetePropertyDateienList = new ArrayList<>();

        when(this.propertyDateiA.getDateiname()).thenReturn("abc.properties");
        when(this.propertyDateiB.getDateiname()).thenReturn("bcd.properties");

        erwartetePropertyDateienList.add(this.propertyDateiA);
        erwartetePropertyDateienList.add(this.propertyDateiB);
        propertyDateienList.add(this.propertyDateiB);
        propertyDateienList.add(this.propertyDateiA);

        // Act
        List<PropertyDatei> aktuellePropertyDateienList =
            this.dateinamenSortierer.sortiereDateinamenAusPropertyDateiList(propertyDateienList);

        // Assert
        assertEquals(aktuellePropertyDateienList, erwartetePropertyDateienList);
    }
}
