package de.bund.bva.isyfact.security.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Privilege {

    @JacksonXmlProperty(isAttribute = true, localName = "Id")
    private String id;

    public String getId() {
        return id;
    }

}
