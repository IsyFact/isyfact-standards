package de.bund.bva.isyfact.ueberwachung.config;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public class LoadbalancerServletConfigurationProperties {

    private String isAliveFileLocation = "/WEB-INF/classes/config/isAlive";;

    @NotBlank
    public String getIsAliveFileLocation() {
        return isAliveFileLocation;
    }

    public void setIsAliveFileLocation(String isAliveFileLocation) {
        this.isAliveFileLocation = isAliveFileLocation;
    }
}
