package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;

/**
 * Hash-Schlüssel für das Caching ermittelter Methoden.
 * 
 */
class MethodHashKey {

    /** Eine RemoteBean-Methode. */
    private final Method method;

    /** Die Zielklasse, die die Zielmethode implementiert. */
    private final Class<?> targetClass;

    /**
     * Erzeugt einen HashKey.
     * 
     * @param method
     *            eine RemoteBean-Methode
     * @param targetClass
     *            die Zielklasse des Anwendungskerns, die die Zielmethode implementiert
     */
    MethodHashKey(Method method, Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MethodHashKey other = (MethodHashKey) obj;
        return method.equals(other.method) && targetClass.equals(other.targetClass);
    }

    @Override
    public int hashCode() {
        return method.hashCode() ^ targetClass.hashCode();
    }

}
