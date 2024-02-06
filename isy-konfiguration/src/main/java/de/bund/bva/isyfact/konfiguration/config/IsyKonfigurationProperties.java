package de.bund.bva.isyfact.konfiguration.config;

import java.util.ArrayList;
import java.util.List;

/**
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
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
