package de.bund.bva.isyfact.security.authentication;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

public class RolePrivilegesMapperTest {

    private RolePrivilegesMapper mapper;

    @BeforeEach
    public void init() {
        mapper = new RolePrivilegesMapper("/resources/sicherheit/rollenrechte.xml");
    }

    @Test
    public void testGetAllPrivileges() {
        assertEquals(3, mapper.getAllPrivileges().size());
    }

    @Test
    public void testMapToPrivilege() {
        assertEquals(3, mapper.getPrivilegesByRoles(Collections.singletonList("Rolle_ABC")).size());
        assertEquals(0, mapper.getPrivilegesByRoles(Collections.singletonList("Rolle_Keine")).size());
        assertEquals(1, mapper.getPrivilegesByRoles(Collections.singletonList("Rolle_B")).size());
    }

    @Test
    public void testAppId() {
        assertEquals("Default", mapper.getApplicationId());
    }

    @Test
    public void testToString() {
        String expected = "AnwendungsId: Default\n"
                + "RollenRechteMapping: {Rolle_C=[Recht_C], Rolle_Keine=[], Rolle_B=[Recht_B], Rolle_A=[Recht_A], Rolle_ABC=[Recht_A, Recht_B, Recht_C]}";
        assertEquals(expected, mapper.toString());
    }

}
