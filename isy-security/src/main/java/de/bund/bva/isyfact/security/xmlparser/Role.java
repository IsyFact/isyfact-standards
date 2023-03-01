package de.bund.bva.isyfact.security.xmlparser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Role {

    @JacksonXmlProperty(isAttribute = true, localName = "RolleId")
    private String id;

    @JacksonXmlProperty(namespace = "tns", localName = "rechtId")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Privilege> privileges;

    public String getId() {
        return id;
    }

    public List<Privilege> getPrivileges() {
        return Optional.ofNullable(privileges).orElseGet(Collections::emptyList);
    }

}
