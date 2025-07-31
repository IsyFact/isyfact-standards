package de.bund.bva.isyfact.logging.impl;



/**
 * Auflistung aller Standard-Logeinträge und deren Nachrichten, die in IsyFact-Logging genutzt werden.
 * 
 */
public enum Ereignisschluessel {

    /** Aufruf einer Methode. */
    EISYLO01001("Methode {} wird aufgerufen."),
    /** Erfolgreiches Ende eines Methodenaufrufs. */
    EISYLO01002("Aufruf von {} erfolgreich beendet."),
    /** Nicht erfolgreiches Ende eines Methodenaufrufs. */
    EISYLO01003("Aufruf von {} mit Fehler beendet."),
    /** Erfolgreiches Ende eines Methodenaufrufs inklusive Dauer. */
    EISYLO01004("Aufruf von {} erfolgreich beendet. Der Aufruf dauerte {} ms."),
    /** Nicht erfolgreiches Ende eines Methodenaufrufs inklusive Dauer. */
    EISYLO01005("Aufruf von {} mit Fehler beendet. Der Aufruf dauerte {} ms."),
    /** Aufruf einer Methode inklusive Parameter. */
    DEBUG_LOGGE_DATEN("Die Methode {} wurde mit folgenden Parametern aufgerufen: {}. Als Ergebnis wurde geliefert: {}"),
    /** Hinweis auf das Aufrufen einer Methode mit einem zu großen Parameter. */
    DEBUG_LOGGE_DATEN_GROESSE("Die Methode {} wurde mit einem zu großen Parameter aufgerufen. Position: {}, Klasse: {}"),
    /** Ersatz für einen zu großen Parameter in Log-Ausgaben. */
    DEBUG_LOGGE_DATEN_PARAMETER_ZU_GROSS("<Maximale Größe überschritten>"),
    /** Ersatz für gekürzte Felder in einer zu großen Log-Nachricht. **/
    DEBUG_LOG_GEKUERZT("<gekürzt>"),
    /** Aufruf eines Nachbarsystems inklusive URL. */
    EISYLO01011("Die Methode {} des Nachbarssystems {} wird unter der URL {} aufgerufen."),
    /** Erfolgreiches Ende eines Nachbarsystemaufrufs inklusive URL. */
    EISYLO01012("Aufruf von {} des Nachbarssystems {} unter der URL {} erfolgreich beendet."),
    /** Nicht erfolgreiches Ende eines Nachbarsystemaufrufs inklusive URL. */
    EISYLO01013("Aufruf von {} des Nachbarssystems {} unter der URL {} mit Fehler beendet."),
    /** Erfolgreiches Ende eines Nachbarsystemaufrufs inklusive URL und Dauer. */
    EISYLO01014(
            "Aufruf von {} des Nachbarssystems {} unter der URL {} erfolgreich beendet. Der Aufruf dauerte {} ms."),
    /** Nicht erfolgreiches Ende eines Nachbarsystemaufrufs inklusive URL und Dauer. */
    EISYLO01015(
            "Aufruf von {} des Nachbarssystems {} unter der URL {} mit Fehler beendet. Der Aufruf dauerte {} ms."),

    /** Starten des Spring-Application-Contexts mit Systemname, Systemart und auslösendes Spring-Event. */
    EISYLO02001("Der ApplicationContext des Systems {} ({}) wurde gestartet oder aktualisiert. Event: {}"),
    /** Stoppen des Spring-Application-Contexts mit Systemname, Systemart und auslösendes Spring-Event. */
    EISYLO02002("Der ApplicationContext des Systems {} ({}) wurde gestopped. Event: {}"),
    /** Version des Systems. */
    EISYLO02003("Die Systemversion ist {}."),
    /** Ausgabe eines Laufzeitparameters. */
    EISYLO02004("Der Laufzeitparameter {} besitzt den Wert {}.");

    /**
     * Die Nachricht des Logschlüssels.
     */
    private final String nachricht;

    /**
     * Konstruktor der Klasse.
     * 
     * @param nachricht
     *            die zum Logschlüssel gehörende Nachricht.
     */
    private Ereignisschluessel(String nachricht) {
        this.nachricht = nachricht;
    }

    /**
     * Liefert den Wert des Attributs 'nachricht'.
     * 
     * @return Wert des Attributs.
     */
    public String getNachricht() {
        return nachricht;
    }

}
