package de.bund.bva.isyfact.sicherheit.common.exception;

/**
 * Diese Exception wird von Methoden geworfen, denen ein benötigter Parameter als null oder leerer String
 * übergeben wird.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class InitialisierungsException extends SicherheitTechnicalException {

    /**
     * Serial UId.
     */
    private static final long serialVersionUID = 9167280068984160593L;

    /**
     * Erstellt die Exception mit der AusnahmeId und Paramtern für den Fehlertext.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param parameter
     *            Die Parameter
     */
    public InitialisierungsException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

    /**
     * Erstellt die Exception mit der AusnahmeId, verursachender Exception und Paramtern für den Fehlertext.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param t
     *            Verursachende Exception
     * @param parameter
     *            Die Parameter
     */
    public InitialisierungsException(String ausnahmeId, Throwable t, String... parameter) {
        super(ausnahmeId, t, parameter);
    }
}
