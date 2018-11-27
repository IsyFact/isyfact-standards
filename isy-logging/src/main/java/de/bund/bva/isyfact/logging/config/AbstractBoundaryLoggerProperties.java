package de.bund.bva.isyfact.logging.config;

/**
 * Properties, die das Loggen an Aufrufgrenzen konfigurieren.
 */
public abstract class AbstractBoundaryLoggerProperties {

    /**
     * Pointcut beschreibt die Stellen im Code, die durch den LogInterceptor geloggt werden sollen.
     */
    private String pointcut;

    /**
     * Kennzeichnen, ob an den Schnittstellen die Dauer eines Aufrufs gelogged werden soll. Default = false.
     */
    private boolean loggeDauer = false;

    /**
     * Kennzeichnen, ob an den Schnittstellen der Start eines Methodenaufrufs gelogged werden soll. Default =
     * false.
     */
    private boolean loggeAufruf = false;

    /**
     * Kennzeichnen, ob an den Schnittstellen das Ergebnis eines Aufrufs (Erfolg/Misserfolg) gelogged werden
     * soll. Default = false.
     */
    private boolean loggeErgebnis = false;

    /**
     * Kennzeichnen, ob an den Schnittstellen immer die kompletten Anfragedaten gelogged werden sollen.
     * Default = false.
     */
    private boolean loggeDaten = false;

    /**
     * Kennzeichnen, ob an den Schnittstellen die übergebenen Parameter des Aufrufs gelogged werden sollen,
     * wenn eine Exception auftritt. Default = false.
     */
    private boolean loggeDatenBeiException = false;

    public boolean isLoggeDauer() {
        return this.loggeDauer;
    }

    public void setLoggeDauer(boolean loggeDauer) {
        this.loggeDauer = loggeDauer;
    }

    public boolean isLoggeAufruf() {
        return this.loggeAufruf;
    }

    public void setLoggeAufruf(boolean loggeAufruf) {
        this.loggeAufruf = loggeAufruf;
    }

    public boolean isLoggeErgebnis() {
        return this.loggeErgebnis;
    }

    public void setLoggeErgebnis(boolean loggeErgebnis) {
        this.loggeErgebnis = loggeErgebnis;
    }

    public boolean isLoggeDaten() {
        return this.loggeDaten;
    }

    public void setLoggeDaten(boolean loggeDaten) {
        this.loggeDaten = loggeDaten;
    }

    public boolean isLoggeDatenBeiException() {
        return this.loggeDatenBeiException;
    }

    public void setLoggeDatenBeiException(boolean loggeDatenBeiException) {
        this.loggeDatenBeiException = loggeDatenBeiException;
    }

    public String getPointcut() {
        return this.pointcut;
    }

    public void setPointcut(String pointcut) {
        this.pointcut = pointcut;
    }

}
