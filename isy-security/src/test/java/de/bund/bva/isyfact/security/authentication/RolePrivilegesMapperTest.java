package de.bund.bva.isyfact.security.authentication;


import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class RolePrivilegesMapperTest {

    private RolePrivilegesMapper mapper;

    @BeforeEach
    public void init() {
        mapper = new RolePrivilegesMapper(new ClassPathResource("/resources/sicherheit/rollenrechte.xml"));
    }

    @Test
    void testGetAllPrivileges() {
        assertThat(mapper.getAllPrivileges()).hasSize(3);
    }

    @Test
    void testMapToPrivilege() {
        assertThat(mapper.getPrivilegesByRoles(Collections.singletonList("Rolle_ABC"))).hasSize(3);
        assertThat(mapper.getPrivilegesByRoles(Collections.singletonList("Rolle_Keine"))).isEmpty();
        assertThat(mapper.getPrivilegesByRoles(Collections.singletonList("Rolle_B"))).hasSize(1);
        assertThat(mapper.getPrivilegesByRoles(Collections.singletonList("unknown"))).isEmpty();
    }

    @Test
    void testAppId() {
        assertThat(mapper.getApplicationId()).isEqualTo("Default");
    }

    @Test
    void testToString() {
        String expected = "AnwendungsId: Default\n"
                + "RollenRechteMapping: {Rolle_C=[Recht_C], Rolle_Keine=[], Rolle_B=[Recht_B], Rolle_A=[Recht_A], Rolle_ABC=[Recht_A, Recht_B, Recht_C]}";

        assertThat(mapper).hasToString(expected);
    }

    @Test
    void testNoRoleMappingFile () {
        mapper = new RolePrivilegesMapper(new ClassPathResource("/resources/sicherheit/noRollenrechte.xml"));

        assertThat(mapper.getAllPrivileges()).isEmpty();
        assertThat(mapper.getRolePrivilegesMap()).isEmpty();
        assertThat(mapper.getApplicationId()).isEmpty();
    }

}
