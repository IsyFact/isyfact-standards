package de.bund.bva.isyfact.ueberwachung.config;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

/**
 * Properties to configure the Loadbalancer servlet.
 */
@Validated
public class LoadbalancerServletConfigurationProperties {


    /**
     * Location of the isAlive file.
     */
    private String isAliveFileLocation = "/WEB-INF/classes/config/isAlive";

    @NotBlank
    public String getIsAliveFileLocation() {
        return isAliveFileLocation;
    }

    public void setIsAliveFileLocation(String isAliveFileLocation) {
        this.isAliveFileLocation = isAliveFileLocation;
    }
}
