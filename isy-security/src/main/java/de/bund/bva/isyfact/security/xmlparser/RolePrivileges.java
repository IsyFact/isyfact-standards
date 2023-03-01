package de.bund.bva.isyfact.security.xmlparser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Anwendung")
public class RolePrivileges {

    @JacksonXmlProperty(localName = "AnwendungsId")
    private String appId;

    @JacksonXmlProperty(namespace = "tns", localName = "rechte")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PrivilegeContainer> privileges;

    @JacksonXmlProperty(namespace = "tns", localName = "rollen")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Role> roles;

    public String getAppId() {
        return appId;
    }

    public List<Privilege> getPrivileges() {
        return Optional.ofNullable(privileges)
                .orElseGet(Collections::emptyList).stream()
                .map(PrivilegeContainer::getPrivilege)
                .collect(Collectors.toList());
    }

    public List<Role> getRoles() {
        return Optional.ofNullable(roles).orElseGet(Collections::emptyList);
    }

}
