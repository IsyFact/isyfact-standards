package de.bund.bva.isyfact.konfiguration.config;

import java.util.ArrayList;
import java.util.List;

public class IsyKonfigurationProperties {

    private String namenschema;

    private List<String> properties = new ArrayList<>();

    public List<String> getProperties() {
        return properties;
    }

    public String getNamenschema() {
        return namenschema;
    }

    public void setNamenschema(String namenschema) {
        this.namenschema = namenschema;
    }
}
