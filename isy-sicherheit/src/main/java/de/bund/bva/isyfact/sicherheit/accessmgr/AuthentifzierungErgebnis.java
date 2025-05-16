package de.bund.bva.isyfact.sicherheit.accessmgr;

import java.io.Serializable;

/** 
 * Interface für Ergebnis des Authentifzierungsaufrufs des {@link AccessManager}
 * Muss Serialisierbar sein für Caching.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public interface AuthentifzierungErgebnis extends Serializable {

}
