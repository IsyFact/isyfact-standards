package de.bund.bva.isyfact.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Collections;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import de.bund.bva.isyfact.security.xmlparser.RolePrivilegesMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class RolePrivilegesMapperTest {

    private RolePrivilegesMapper mapper = new RolePrivilegesMapper(new ClassPathResource("/resources/sicherheit/rollenrechte.xml"));

    static Logger sutLogger = (Logger) LoggerFactory.getLogger(RolePrivilegesMapper.class);

    private Level logLevel;

    static ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void init() {
        listAppender = new ListAppender<>();
        sutLogger.setLevel(Level.DEBUG);
        logLevel = sutLogger.getLevel();

        listAppender.start();
        sutLogger.addAppender(listAppender);
    }

    @AfterEach
    void restoreLogger() {
        sutLogger.detachAppender(listAppender);
        listAppender.stop();
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

        // verify logging info
        final String expectedMsg = String.format("Rollenrechte-Mapping Datei unter class path resource [%s] nicht gefunden.",
            "resources/sicherheit/noRollenrechte.xml");

        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
            .containsExactly(tuple(logLevel, expectedMsg));
    }

    @Test
    void testRoleMappingFileNoApplicationIdSet () {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy((ThrowableAssert.ThrowingCallable) () ->
                new RolePrivilegesMapper(new ClassPathResource("/resources/sicherheit/rollenrechte-no-application-id.xml"))
            ).withMessageMatching(".*: Es ist keine AnwendungsId gesetzt");
    }

}
