package de.bund.bva.isyfact.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PrivilegeVoter implements AccessDecisionVoter<Object> {

    public static final String PRIVELEGE_PREFIX = "PRIV_";

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(privilege -> !privilege.startsWith(PRIVELEGE_PREFIX))
                .findAny()
                .map(s -> ACCESS_DENIED)
                .orElse(ACCESS_GRANTED);
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
