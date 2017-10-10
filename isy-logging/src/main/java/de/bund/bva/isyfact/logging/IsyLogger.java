package de.bund.bva.isyfact.logging;

/**
 * Interface zum Erstellen beliebiger Logeinträge. Aufgrund der Menge an angebotenen Methoden sollten dieses
 * Interface nur in begründeten Ausnahmefällen benutzt werden.
 */
public interface IsyLogger extends IsyLoggerStandard, IsyLoggerFachdaten, IsyLoggerTypisiert {

    /**
     * Prüft, ob das Log-Level 'Trace' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    boolean isTraceEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Debug' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    boolean isDebugEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Info' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    boolean isInfoEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Warn' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    boolean isWarnEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Error' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    boolean isErrorEnabled();
    
    /**
     * Prüft, ob das Log-Level 'Fatal' aktiviert ist.
     * 
     * @return <code>true</code> falls das Level aktiv ist, <code>false</code> sonst.
     */
    boolean isFatalEnabled();

}
