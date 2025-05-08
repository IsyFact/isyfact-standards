package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test;

import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public interface ValidRemoteBean {

    void eineMethode() throws PlisTechnicalToException;

    void eineAndereMethode() throws PlisTechnicalToException;

    void methodeMitException() throws TechnicalException, PlisTechnicalToException;

    void methodeMitParametern(Integer zahl, String zeichenkette) throws PlisTechnicalToException;
}
