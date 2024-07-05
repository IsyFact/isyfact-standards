package de.bund.bva.isyfact.security.authentication;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.security.IsySecurityTestConfiguration;

@SpringBootTest(classes = {IsySecurityTestConfiguration.class, AuthorityPrefixTest.AnnotationTest.class})
public class AuthorityPrefixTest {

    private static final String[] TEST_AUTHORITIES = {"PRIV_test"};

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
