package de.bund.bva.pliscommon.sicherheit.config;

public class IsySicherheitConfigurationProperties {

    /** Defaultwert für die Time-to-live (in Sekunden) der Cacheinträge. 0 = deaktiviert. **/
    private int ttl = 0;

    /** Defaultwert für die maximale Anzahl an Einträgen im Cache (in Memory). **/
    private int maxelements = 10000;

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getMaxelements() {
        return maxelements;
    }

    public void setMaxelements(int maxelements) {
        this.maxelements = maxelements;
    }

}
