package de.bund.bva.isyfact.security.test;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.security.xmlparser.RolePrivilegeMapper;

public class RolePrivilegesMapperTest {

    private RolePrivilegeMapper mapper;

    @BeforeEach
    public void init() {
        this.mapper = new RolePrivilegeMapper("/resources/sicherheit/rollenrechte.xml");
    }

    @Test
    public void testGetAllPrivileges() {
        assert mapper.getAllPrivileges().size() == 5;
    }

    @Test
    public void testMapToPrivilege() {
        assert mapper.getPrivilegesByRoles(Collections.singletonList("Rolle1")).size() == 3;
        assert mapper.getPrivilegesByRoles(Collections.singletonList("Rolle2")).size() == 2;
    }

    // TODO toString kann auch getestet werden

}
