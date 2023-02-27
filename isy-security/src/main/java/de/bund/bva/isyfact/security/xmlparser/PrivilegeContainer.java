package de.bund.bva.isyfact.security.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PrivilegeContainer {
    @JacksonXmlProperty(namespace = "tns", localName = "rechtId")
    private Privilege privilege;

    public Privilege getPrivilege() {return this.privilege;}
}
