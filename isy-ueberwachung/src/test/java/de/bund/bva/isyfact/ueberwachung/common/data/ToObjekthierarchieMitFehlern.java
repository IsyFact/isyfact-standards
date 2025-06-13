package de.bund.bva.isyfact.ueberwachung.common.data;

public class ToObjekthierarchieMitFehlern {

    private int testPrimitive;

    private String testString;

    private ToMitFehlerFeld to;

    private ToMitFehlerCollection toColl;

    public int getTestPrimitive() {
        return this.testPrimitive;
    }

    public void setTestPrimitive(int testPrimitive) {
        this.testPrimitive = testPrimitive;
    }

    public String getTestString() {
        return this.testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public ToMitFehlerFeld getTo() {
        return this.to;
    }

    public void setTo(ToMitFehlerFeld to) {
        this.to = to;
    }

    public ToMitFehlerCollection getToColl() {
        return this.toColl;
    }

    public void setToColl(ToMitFehlerCollection toColl) {
        this.toColl = toColl;
    }

}
