package de.bund.bva.isyfact.konfiguration;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.bund.bva.isyfact.konfiguration.common.KonfigurationChangeListener;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.isyfact.konfiguration.common.impl.PropertyKonfiguration;
import de.bund.bva.isyfact.konfiguration.common.impl.ReloadablePropertyKonfiguration;

import junit.framework.TestCase;

public class TestKonfiguration extends TestCase {

    final static String DEFAULTNAMENSSCHEMA = ".*[.]properties";

    private static final String CONFIG_A = "/config/config_A.properties";

    private static final String CONFIG_B = "/config/config_B.properties";

    private static final String CONFIG_CHANGE = "/config/config_Change.properties";

    private static final String CONFIG_ORDNER = "/config/config_ordner/";

    public void testKonfigLaden() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_A });
        assertEquals(false, konfig.getAsBoolean("parameter.boolean"));
        assertEquals(true, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals(true, konfig.getAsBoolean("parameter.boolean.3"));
        assertEquals(1000, konfig.getAsInteger("parameter.int"));
        assertEquals(1000, konfig.getAsInteger("parameter.int.2"));
        assertEquals(100.5, konfig.getAsDouble("parameter.double"));
        assertEquals("Hans", konfig.getAsString("parameter.rawstring"));
        assertEquals("Hans ", konfig.getAsRawString("parameter.rawstring"));
        assertEquals("", konfig.getAsString("parameter.defined"));
        assertEquals("A", konfig.getAsString("parameter.null", "A"));
        assertNull(konfig.getAsString("parameter.null", null));
        assertEquals(8, konfig.getSchluessel().size());
    }

    public void testKonfigUeberschreiben() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_A, CONFIG_B });
        assertEquals(false, konfig.getAsBoolean("parameter.boolean"));
        assertEquals(false, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals(2000, konfig.getAsInteger("parameter.int"));
        assertEquals(100.5, konfig.getAsDouble("parameter.double"));
        assertEquals("Hugo", konfig.getAsString("parameter.string"));
        assertEquals("Hans", konfig.getAsString("parameter.rawstring"));
        assertEquals("Hans ", konfig.getAsRawString("parameter.rawstring"));
        assertEquals(9, konfig.getSchluessel().size());
    }

    public void testKonfigLadenError() throws Exception {
        try {
            new ReloadablePropertyKonfiguration(new String[] { "ddd" });
            fail("Der Test hätte eine " + KonfigurationDateiException.class.getName() + " werfen müssen.");
        } catch (KonfigurationDateiException ex) {
            System.out.println("Erwartete Ausnahme: " + ex);
        }
    }

    public void testKonfigParamError() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_A });
        try {
            konfig.getAsDouble("nicht.vorhanden");
            fail(
                "Der Test hätte eine " + KonfigurationParameterException.class.getName() + " werfen müssen.");
        } catch (KonfigurationParameterException ex) {
            System.out.println("Erwartete Ausnahme: " + ex);
        }

        try {
            konfig.getAsInteger("parameter.boolean");
            fail(
                "Der Test hätte eine " + KonfigurationParameterException.class.getName() + " werfen müssen.");
        } catch (KonfigurationParameterException ex) {
            System.out.println("Erwartete Ausnahme: " + ex);
        }
    }

    public void testNeuLaden() throws Exception {
        URL configUrl = TestKonfiguration.class.getResource(CONFIG_CHANGE);
        File configFile = new File(configUrl.toURI());
        FileWriter writer = new FileWriter(configFile);
        writer.write("parameter.a = Hello\n");
        writer.close();

        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_CHANGE });

        assertEquals(konfig.getAsString("parameter.a"), "Hello");

        final Set<String> myChangedKeys = new HashSet<String>();
        konfig.addKonfigurationChangeListener(new KonfigurationChangeListener() {
            @Override
            public void onKonfigurationChanged(Set<String> changedKeys) {
                myChangedKeys.addAll(changedKeys);
            }
        });

        Thread.sleep(1000);

        writer = new FileWriter(configFile);
        writer.write("parameter.a = World\n");
        writer.write("parameter.b = Neu\n");
        writer.close();

        assertTrue(konfig.checkAndUpdate());
        assertEquals(konfig.getAsString("parameter.a"), "World");
        assertEquals(konfig.getAsString("parameter.b"), "Neu");

        assertEquals(myChangedKeys.size(), 2);
        assertTrue(myChangedKeys.contains("parameter.a"));
        assertTrue(myChangedKeys.contains("parameter.b"));
    }

    public void testPropertiyKonfigurationAlsStreamLaden() throws Exception {
        PropertyKonfiguration konfig =
            new PropertyKonfiguration(Collections.singletonList(CONFIG_A), DEFAULTNAMENSSCHEMA);
        assertEquals(1000, konfig.getAsInteger("parameter.int"));
    }

    public void testKonfigLoeschenNeuLadenError() throws Exception {
        String classpathRoot = "/";
        String neueDateiName = "config_temp.properties";
        URL url = TestKonfiguration.class.getResource(classpathRoot);
        File configFile = new File(url.toURI().resolve(neueDateiName));
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            writer.write("parameter.a = Hello\n");
            writer.close();

            ReloadablePropertyKonfiguration konfig =
                new ReloadablePropertyKonfiguration(new String[] { classpathRoot + neueDateiName });

            assertEquals("Hello", konfig.getAsString("parameter.a"));
            configFile.delete();
            assertTrue(konfig.checkAndUpdate());
            fail(
                "Der Test hätte eine " + KonfigurationParameterException.class.getName() + " werfen müssen.");
        } catch (KonfigurationDateiException ex) {
            System.out.println("Erwartete Ausnahme: " + ex);
        } finally {
            // Die Datei muss unbedingt gelöscht werden, sonst werden andere Tests beeinflusst.
            configFile.delete();
        }
    }

    public void testKonfigAusPropertyKonfigurationLaden() {
        PropertyKonfiguration konfig = new PropertyKonfiguration(
            Arrays.asList(new String[] { CONFIG_ORDNER, CONFIG_B }), DEFAULTNAMENSSCHEMA);
        assertEquals(1000, konfig.getAsInteger("parameter.ordner.int"));
        assertEquals(2000, konfig.getAsInteger("parameter.int"));
        assertEquals(true, konfig.getAsBoolean("parameter.ordner.boolean"));
        assertEquals(false, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals("test", konfig.getAsString("parameter.ordner.string"));
        assertEquals("Hugo", konfig.getAsString("parameter.string"));

        assertEquals(6, konfig.getSchluessel().size());
    }

    public void testKonfigAusOrdnerLaden() {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_ORDNER });
        assertEquals(1000, konfig.getAsInteger("parameter.ordner.int"));
        assertEquals(true, konfig.getAsBoolean("parameter.ordner.boolean"));
        assertEquals("test", konfig.getAsString("parameter.ordner.string"));
        assertEquals(3, konfig.getSchluessel().size());
    }

    public void testKonfigAusOrdnerUndDateiLaden() {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_ORDNER, CONFIG_B });
        assertEquals(1000, konfig.getAsInteger("parameter.ordner.int"));
        assertEquals(2000, konfig.getAsInteger("parameter.int"));
        assertEquals(true, konfig.getAsBoolean("parameter.ordner.boolean"));
        assertEquals(false, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals("test", konfig.getAsString("parameter.ordner.string"));
        assertEquals("Hugo", konfig.getAsString("parameter.string"));

        assertEquals(6, konfig.getSchluessel().size());
    }

    public void testPropertiesDateiAusOrdnerLoeschen() throws Exception {
        String neueDateiName = "config_temp.properties";

        URL configUrl = TestKonfiguration.class.getResource(CONFIG_ORDNER);
        File configFile = new File(configUrl.toURI().resolve(neueDateiName));
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            writer.write("parameter.a = Hello\n");
            writer.close();

            ReloadablePropertyKonfiguration konfig =
                new ReloadablePropertyKonfiguration(new String[] { CONFIG_ORDNER });

            int anzahlSchluessel = konfig.getSchluessel().size();
            assertEquals("Hello", konfig.getAsString("parameter.a"));

            final Set<String> myChangedKeys = new HashSet<String>();
            konfig.addKonfigurationChangeListener(new KonfigurationChangeListener() {
                @Override
                public void onKonfigurationChanged(Set<String> changedKeys) {
                    myChangedKeys.addAll(changedKeys);
                }
            });
            configFile.delete();
            Thread.sleep(200);
            assertTrue(konfig.checkAndUpdate());
            assertEquals(anzahlSchluessel - 1, konfig.getSchluessel().size());
            assertEquals(myChangedKeys.size(), 1);
            assertTrue(myChangedKeys.contains("parameter.a"));

            try {
                konfig.getAsString("parameter.a");
                fail("Der Test hätte eine " + KonfigurationParameterException.class.getName()
                    + " werfen müssen.");
            } catch (KonfigurationParameterException ex) {
                System.out.println("Erwartete Ausnahme: " + ex);
            }
        } finally {
            // Die Datei muss unbedingt gelöscht werden, sonst werden andere Tests beeinflusst.
            configFile.delete();
        }
    }

    public void testPropertiesDateiInOrdnerHinzufuegen() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_ORDNER });

        int anzahlSchluessel = konfig.getSchluessel().size();
        final Set<String> myChangedKeys = new HashSet<String>();
        konfig.addKonfigurationChangeListener(new KonfigurationChangeListener() {
            @Override
            public void onKonfigurationChanged(Set<String> changedKeys) {
                myChangedKeys.addAll(changedKeys);
            }
        });

        String neueDateiName = "config_temp.properties";
        URL configUrl = TestKonfiguration.class.getResource(CONFIG_ORDNER);
        File configFile = new File(configUrl.toURI().resolve(neueDateiName));
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            writer.write("parameter.a = Hello\n");
            writer.close();

            Thread.sleep(200);
            assertTrue(konfig.checkAndUpdate());
            Thread.sleep(200);
            assertEquals(konfig.getSchluessel().size(), anzahlSchluessel + 1);

            assertEquals(myChangedKeys.size(), 1);
            assertTrue(myChangedKeys.contains("parameter.a"));
            assertEquals("Hello", konfig.getAsString("parameter.a"));
        } finally {
            // Die Datei muss unbedingt gelöscht werden, sonst werden andere Tests beeinflusst.
            configFile.delete();
        }
    }
}
