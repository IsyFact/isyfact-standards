package de.bund.bva.isyfact.logging;

/*
 * #%L
 * isy-logging-log4j-bridge
 * %%
 * 
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.log4jbridge.Log4jDiagnosticContextAdapter;
import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * Testfälle des Log4jDiagnosticContextAdapter.
 */
public class Log4jDiagnosticContextAdapterTest {

    /** Konstante für den verwendeten "Key" in den Tests. */
    private static final String SCHLUESSEL = "einschluessel";

    /** Konstante für den verwendeten "Value" in den Tests. */
    private static final String WERT = "einwert";

    /**
     * Testfall für das Hinzufügen und Entfenen von Schlüssel/Wert-Paaren. Es wird überprüft, dass der Adapter
     * sich analog zum direkten Aufruf von MDC verhält.
     */
    @Test
    public void testPutRemove() {
        Log4jDiagnosticContextAdapter adapter = new Log4jDiagnosticContextAdapter();

        pruefeMdcUndAdapter(adapter, SCHLUESSEL);
        adapter.put(SCHLUESSEL, WERT);
        adapter.put(SCHLUESSEL, WERT);
        pruefeMdcUndAdapter(adapter, SCHLUESSEL);
        adapter.remove(SCHLUESSEL);
        pruefeMdcUndAdapter(adapter, SCHLUESSEL);
        adapter.put(SCHLUESSEL, WERT);
        pruefeMdcUndAdapter(adapter, SCHLUESSEL);
        adapter.clear();
        pruefeMdcUndAdapter(adapter, SCHLUESSEL);
    }

    /**
     * Prüft ob der direkte Zugriff über den MDC die gleichen Ergebnisse liefert wie der Adapter.
     * 
     * @param adapter
     *            der zu testende Adapter.
     * @param schluessel
     *            Schluessel, dessen Wert verglichen werden soll.
     */
    private void pruefeMdcUndAdapter(Log4jDiagnosticContextAdapter adapter, String schluessel) {
        Assert.assertEquals(MDC.get(schluessel), adapter.get(schluessel));
        Hashtable<?, ?> context = MDC.getContext();
        Map<?, ?> copyOfContextMap = adapter.getCopyOfContextMap();

        if (context != null || copyOfContextMap != null) {
            Assert.assertEquals(context.size(), copyOfContextMap.size());
        }
    }

    /**
     * Testfall für das Hinzufügen und Entfenen von Schlüssel/Wert-Paaren über das Setzen der Kontextmap. Es
     * wird überprüft, dass der Adapter sich analog zum direkten Aufruf von MDC verhält.
     */
    @Test
    public void testPutRemoveContextMap() {

        Map<String, Object> contextMap = new HashMap<String, Object>();
        contextMap.put("001", "A");
        contextMap.put("002", "B");
        contextMap.put("003", "C");
        contextMap.put("004", new Integer(5));

        Log4jDiagnosticContextAdapter adapter = new Log4jDiagnosticContextAdapter();
        adapter.setContextMap(contextMap);

        Hashtable<?, ?> context = MDC.getContext();
        Map<?, ?> copyOfContextMap = adapter.getCopyOfContextMap();

        Assert.assertEquals(context.size(), copyOfContextMap.size());
        for (Object schluessel : context.keySet()) {
            // ACHTUNG: Der MDC von SLF4J erlaubt nur String-Werte, log4j beliebige Objekte. Im Adapter werden
            // "nicht-string-werte" zu String umgewandelt. Um allgemeine Objekte überprüfen zu können, muss
            // das Ergebnis aus log4j daher in String umgewandelt werden.
            Assert.assertEquals("" + MDC.get((String) schluessel), adapter.get((String) schluessel));
        }

    }

    /**
     * Testfall zum Testen ob Adapter und MDC sich im Fehlerfall gleich verhalten
     */
    @Test
    public void testException() {

        Log4jDiagnosticContextAdapter adapter = new Log4jDiagnosticContextAdapter();

        // Beim Setzen eines Null-Wertes wird eine Nullpointer-Exception erwartet.
        NullPointerException npeAdapter = null;
        NullPointerException npeMDC = null;

        try {
            adapter.put("001", null);
        } catch (NullPointerException npe) {
            npeAdapter = npe;
        }

        try {
            MDC.put("001", null);
        } catch (NullPointerException npe) {
            npeMDC = npe;
        }

        Assert.assertNotNull(npeAdapter);
        Assert.assertNotNull(npeMDC);

    }

    /**
     * Testfall für das Hinzufügen und Entfenen von Korrelations-IDs. Es wird überprüft, dass der Adapter sich
     * analog zum direkten Aufruf von MDC verhält. Zudem wird geprüft, dass die Korrelations-ID beim setzen im
     * MDC auch im NDC gesetzt wird.
     */
    @Test
    public void testPutRemoveKorrelationsId() {
        Log4jDiagnosticContextAdapter adapter = new Log4jDiagnosticContextAdapter();

        // Wir setzen 3-mal die Korerlations-ID im NDC. Dies entspricht einem Ersetzen.
        adapter.put(MdcHelper.MDC_KORRELATIONS_ID, "1");
        adapter.put(MdcHelper.MDC_KORRELATIONS_ID, "1;2");
        adapter.put(MdcHelper.MDC_KORRELATIONS_ID, "1;2;3");
        String korrIdAdapter = adapter.get(MdcHelper.MDC_KORRELATIONS_ID);
        String korrIdNDC = NDC.peek();
        Assert.assertEquals(korrIdAdapter, korrIdNDC);

        adapter.remove(MdcHelper.MDC_KORRELATIONS_ID);
        NDC.pop();

        korrIdAdapter = adapter.get(MdcHelper.MDC_KORRELATIONS_ID);
        korrIdNDC = NDC.peek();

        // Akzeptierte Abweichung: log4j liefert bei einem "peek" niemals null, sondern einen Leerstring. Bei
        // einem "get" auf dem MDC wird null geliefert.
        Assert.assertNull(korrIdAdapter);
        Assert.assertEquals(korrIdNDC, "");

    }

}
