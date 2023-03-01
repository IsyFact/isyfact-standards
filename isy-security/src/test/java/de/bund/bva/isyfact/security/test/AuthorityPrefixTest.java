package de.bund.bva.isyfact.security.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.security.example.IsySpringBootApplication;

@Disabled("currently only runs as an integration test because of the oauth2 test config")
@SpringBootTest(classes = { IsySpringBootApplication.class, AuthorityPrefixTest.AnnotationTest.class })
public class AuthorityPrefixTest {

    private static final String[] TEST_AUTHORITIES = { "PRIV_test", "ROLE_test" };

    @Autowired
    private AnnotationTest annotationTest;

    @BeforeEach
    public void setup() {
        TestingAuthenticationToken token = new TestingAuthenticationToken("test", "test", TEST_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    public void shouldAllowMethodeSecuredWithPrivPrefix() {
        annotationTest.privPrefix();
    }

    @Test
    public void shouldDenyMethodSecuredWithRolePrefix() {
        assertThrows(AccessDeniedException.class, () -> annotationTest.rolePrefix());
    }

    static class AnnotationTest {

        @Secured("PRIV_test")
        public void privPrefix() {
        }

        @Secured("ROLE_test")
        public void rolePrefix() {
        }

    }

}
