package de.bund.bva.isyfact.security.xmlparser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Anwendung")
public class RolePrivileges {

    @JacksonXmlProperty(localName = "AnwendungsId")
    private String appId;

    @JacksonXmlProperty(namespace = "tns", localName = "rechte")
    @JacksonXmlElementWrapper(useWrapping = false)
    private PrivilegeContainer[] privilegeContainers;

    @JacksonXmlProperty(namespace = "tns", localName = "rollen")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Role[] roles;

    public String getAppId() {return this.appId;}

    public Privilege[] getPrivileges() {
        return Arrays.stream(this.privilegeContainers)
                .map(container -> container.getPrivilege())
                .toArray(Privilege[]::new);
    }

    public Role[] getRoles() {
        return this.roles;
    }
}
