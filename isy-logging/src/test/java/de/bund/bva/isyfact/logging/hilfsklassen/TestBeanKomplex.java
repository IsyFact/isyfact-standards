package de.bund.bva.isyfact.logging.hilfsklassen;

/*
 * #%L
 * isy-logging
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

import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import de.bund.bva.isyfact.logging.impl.LogErrorKategorie;

import ch.qos.logback.classic.PatternLayout;

/**
 * Bean zum Testen der Serialisierung.
 */
public class TestBeanKomplex extends AbstractTestBeanKomplex implements TestBeanInterface {

    /** Liste von Enums. */
    private List<LogErrorKategorie> eineEnumListe;

    /** Array von Strings. */
    private String[] einStringArray;

    /** Array von Objecten. */
    private Object[] einObjectArray;

    /** Array von ObjeEnumscten. */
    private LogErrorKategorie[] einEnumArray;

    /** Object von java/lang. */
    private ThreadGroup javaLang;

    /** Object von java/util. */
    private Formatter javaUtil;

    /** Ein Enum-Wert. */
    private LogErrorKategorie einEnum;

    /** Eine Object aus einer Drittbibliothek. */
    private PatternLayout extern;

    /** String-Property, die auf null gesetzt wird. */
    private String nullString;

    /** Enthält eine Referenz auf das Object selbst - mit Datentyp Object. */
    private Object rekursivObject;

    /** Enthält eine Referenz auf das Object selbst. */
    private TestBeanKomplex rekursiv;

    /** Enthält eine Referenz auf eine andere Instanz von TestBeanKomplex. */
    private TestBeanKomplex rekursivNeu;

    /** Ein Integer Wert. */
    private Integer einInteger;

    /**
     * Konstruktor der Klasse. Initialisiert die Properties
     * 
     * @param rekursiv
     *            gibt an, ob rekursive Objekte erzeugt werden sollen (also die Properties der Klasse, die
     *            selbst wieder vom Typ-Testbean sind mit 'this' gefüllt werden.
     */
    public TestBeanKomplex(boolean rekursiv) {
        super(rekursiv);
        this.eineEnumListe = Arrays.asList(LogErrorKategorie.ERROR, LogErrorKategorie.FATAL,
                LogErrorKategorie.ERROR);
        this.einStringArray = new String[] {"A", "B", null, "C" };
        this.einObjectArray = getEineObjectListe().toArray();
        this.einEnumArray = eineEnumListe.toArray(new LogErrorKategorie[3]);
        this.javaLang = new ThreadGroup("A");
        this.javaUtil = new Formatter();
        this.einEnum = LogErrorKategorie.ERROR;
        this.extern = new PatternLayout();
        this.nullString = null;
        this.rekursiv = this;
        if (rekursiv) {
            rekursivNeu = new TestBeanKomplex(false);
            rekursivObject = this;
        }
        this.einInteger = 123;
    }

    /**
     * Liefert den Wert des Attributs 'eineEnumListe'.
     * 
     * @return Wert des Attributs.
     */
    public List<LogErrorKategorie> geteineEnumListe() {
        return eineEnumListe;
    }

    /**
     * Setzt den Wert des Attributs 'eineEnumListe'.
     * 
     * @param eineEnumListe
     *            Neuer Wert des Attributs.
     */
    public void setEineEnumListe(List<LogErrorKategorie> eineEnumListe) {
        this.eineEnumListe = eineEnumListe;
    }

    /**
     * Liefert den Wert des Attributs 'einStringArray'.
     * 
     * @return Wert des Attributs.
     */
    public String[] getEinStringArray() {
        return einStringArray;
    }

    /**
     * Setzt den Wert des Attributs 'einStringArray'.
     * 
     * @param einStringArray
     *            Neuer Wert des Attributs.
     */
    public void setEinStringArray(String[] einStringArray) {
        this.einStringArray = einStringArray;
    }

    /**
     * Liefert den Wert des Attributs 'einObjectArray'.
     * 
     * @return Wert des Attributs.
     */
    public Object[] getEinObjectArray() {
        return einObjectArray;
    }

    /**
     * Setzt den Wert des Attributs 'einObjectArray'.
     * 
     * @param einObjectArray
     *            Neuer Wert des Attributs.
     */
    public void setEinObjectArray(Object[] einObjectArray) {
        this.einObjectArray = einObjectArray;
    }

    /**
     * Liefert den Wert des Attributs 'einEnumArray'.
     * 
     * @return Wert des Attributs.
     */
    public LogErrorKategorie[] geteinEnumArray() {
        return einEnumArray;
    }

    /**
     * Setzt den Wert des Attributs 'einEnumArray'.
     * 
     * @param einEnumArray
     *            Neuer Wert des Attributs.
     */
    public void setEinEnumArray(LogErrorKategorie[] einEnumArray) {
        this.einEnumArray = einEnumArray;
    }

    /**
     * Liefert den Wert des Attributs 'javaLang'.
     * 
     * @return Wert des Attributs.
     */
    public ThreadGroup getJavaLang() {
        return javaLang;
    }

    /**
     * Setzt den Wert des Attributs 'javaLang'.
     * 
     * @param javaLang
     *            Neuer Wert des Attributs.
     */
    public void setJavaLang(ThreadGroup javaLang) {
        this.javaLang = javaLang;
    }

    /**
     * Liefert den Wert des Attributs 'javaUtil'.
     * 
     * @return Wert des Attributs.
     */
    public Formatter getJavaUtil() {
        return javaUtil;
    }

    /**
     * Setzt den Wert des Attributs 'javaUtil'.
     * 
     * @param javaUtil
     *            Neuer Wert des Attributs.
     */
    public void setJavaUtil(Formatter javaUtil) {
        this.javaUtil = javaUtil;
    }

    /**
     * Liefert den Wert des Attributs 'einEnum'.
     * 
     * @return Wert des Attributs.
     */
    public LogErrorKategorie getEinEnum() {
        return einEnum;
    }

    /**
     * Setzt den Wert des Attributs 'einEnum'.
     * 
     * @param einEnum
     *            Neuer Wert des Attributs.
     */
    public void setEinEnum(LogErrorKategorie einEnum) {
        this.einEnum = einEnum;
    }

    /**
     * Liefert den Wert des Attributs 'extern'.
     * 
     * @return Wert des Attributs.
     */
    public PatternLayout getExtern() {
        return extern;
    }

    /**
     * Setzt den Wert des Attributs 'extern'.
     * 
     * @param extern
     *            Neuer Wert des Attributs.
     */
    public void setExtern(PatternLayout extern) {
        this.extern = extern;
    }

    /**
     * Liefert den Wert des Attributs 'nullString'.
     * 
     * @return Wert des Attributs.
     */
    public String getNullString() {
        return nullString;
    }

    /**
     * Setzt den Wert des Attributs 'nullString'.
     * 
     * @param nullString
     *            Neuer Wert des Attributs.
     */
    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    /**
     * Liefert den Wert des Attributs 'rekursivObject'.
     * 
     * @return Wert des Attributs.
     */
    public Object getRekursivObject() {
        return rekursivObject;
    }

    /**
     * Setzt den Wert des Attributs 'rekursivObject'.
     * 
     * @param rekursivObject
     *            Neuer Wert des Attributs.
     */
    public void setRekursivObject(Object rekursivObject) {
        this.rekursivObject = rekursivObject;
    }

    /**
     * Liefert den Wert des Attributs 'rekursiv'.
     * 
     * @return Wert des Attributs.
     */
    public TestBeanKomplex getRekursiv() {
        return rekursiv;
    }

    /**
     * Setzt den Wert des Attributs 'rekursiv'.
     * 
     * @param rekursiv
     *            Neuer Wert des Attributs.
     */
    public void setRekursiv(TestBeanKomplex rekursiv) {
        this.rekursiv = rekursiv;
    }

    /**
     * Liefert den Wert des Attributs 'rekursivNeu'.
     * 
     * @return Wert des Attributs.
     */
    public TestBeanKomplex getRekursivNeu() {
        return rekursivNeu;
    }

    /**
     * Setzt den Wert des Attributs 'rekursivNeu'.
     * 
     * @param rekursivNeu
     *            Neuer Wert des Attributs.
     */
    public void setRekursivNeu(TestBeanKomplex rekursivNeu) {
        this.rekursivNeu = rekursivNeu;
    }

    /**
     * Liefert den Wert des Attributs 'einInteger'.
     * 
     * @return Wert des Attributs.
     */
    public Integer getEinInteger() {
        return einInteger;
    }

    /**
     * Setzt den Wert des Attributs 'einInteger'.
     * 
     * @param einInteger
     *            Neuer Wert des Attributs.
     */
    public void setEinInteger(Integer einInteger) {
        this.einInteger = einInteger;
    }

    @Override
    public String toString() {
        return "Komplexes Testbean";
    }
}
