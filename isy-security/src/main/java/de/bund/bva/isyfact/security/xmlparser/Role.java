package de.bund.bva.isyfact.security.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Role {
    @JacksonXmlProperty(isAttribute = true, localName = "RolleId")
    private String roleId;

    @JacksonXmlProperty(namespace = "tns", localName = "rechtId")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Privilege[] privileges;

    public String getRoleId() {return roleId;}

    public Privilege[] getPrivileges() {return privileges;}
}
