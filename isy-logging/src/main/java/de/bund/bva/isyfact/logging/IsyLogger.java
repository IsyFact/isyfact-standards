package de.bund.bva.isyfact.logging;

/**
 * Spezifisches Logger-Interface gemäß des Bausteins IsyFact-Logging. Diese Klasse wird zum Erstellen von
 * Logeinträgen verwendet.
 * 
 */
public interface IsyLogger extends IsyLoggerStandard, IsyLoggerFachdaten, IsyLoggerTypisiert {

    /**
     * Prüft, ob das Log-Level 'Trace' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isTraceEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Debug' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isDebugEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Info' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isInfoEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Warn' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isWarnEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Error' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isErrorEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Fatal' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    public boolean isFatalEnabled();

}
