package de.bund.bva.isyfact.logging.util;

/**
 * Interface für BeanConverter zur Serialisierung von Beans bei der Logausgabe.
 *
 */
public interface BeanConverter {

    /**
     * Konvertiert das übergebene Bean in eine Repräsentation, die direkt als Wert für einen Platzhalter in
     * einer Logausgabe verwendet werden kann.
     * 
     * @param bean
     *            das zu konvertierende Bean.
     * @return das konvertierte Bean.
     */
    public Object convert(Object bean);

}