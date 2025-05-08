package de.bund.bva.isyfact.konfiguration.common.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;

public class TestPropertyDatei {

    private static final String CONFIG_X_CLASSPATH = "target/test-classes/config/config_X.properties";
    private static final String CONFIG_X_RESOURCEPATH = "/config/config_X.properties";
    private static final String CONTENT = "parameter.string = Hello";

    @Test
    public void testNeuLaden() throws IOException {
        File f = new File(CONFIG_X_CLASSPATH);
        assertTrue(f.createNewFile());
        FileWriter writer = new FileWriter(f);
        writer.write(CONTENT);
        writer.close();

        PropertyDatei datei = new PropertyDatei(CONFIG_X_RESOURCEPATH);

        assertTrue(f.delete());
        try {
            datei.neuLaden();
            fail("KonfigurationDateiException nicht geworfen.");
        } catch (KonfigurationDateiException e) {
            // Ziel des Tests
        }
    }
    
    @Test
    public void testIsNeueVersionVerfuegbar() throws IOException, InterruptedException {
        File f = new File(CONFIG_X_CLASSPATH);
        assertTrue(f.createNewFile());
        FileWriter writer = new FileWriter(f);
        writer.write(CONTENT);
        writer.close();

        PropertyDatei datei = new PropertyDatei(CONFIG_X_RESOURCEPATH);
        assertFalse(datei.isNeueVersionVerfuegbar());
        Thread.sleep(1000);
        writer = new FileWriter(f);
        writer.write(CONTENT);
        writer.close();
        
        //datei.neuLaden();
        assertTrue(datei.isNeueVersionVerfuegbar());
        assertTrue(f.delete());
        assertEquals(CONFIG_X_RESOURCEPATH, datei.getDateiname());
    }
}
