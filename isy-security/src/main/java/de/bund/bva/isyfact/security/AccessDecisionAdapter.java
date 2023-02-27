package de.bund.bva.isyfact.security;

import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;

@Component
public class AccessDecisionAdapter implements AuthorizationManager<RequestAuthorizationContext> {

    private PrivilegeVoter voter;

    public AccessDecisionAdapter(PrivilegeVoter voter) {
        this.voter = voter;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        // TODO get authority that is needed to access requested resource
        String authority = "dummy";
        int decision = this.voter.vote(authentication.get(), null, null);
        if (decision == ACCESS_GRANTED) {
            return new AuthorizationDecision(hasAuthorization(authentication.get(), authority));
        }
        if (decision == ACCESS_DENIED) {
            return new AuthorizationDecision(false);
        }
        return null; // abstain
    }

    @Override
    public void verify(Supplier authentication, RequestAuthorizationContext context) {
        AuthorizationManager.super.verify(authentication, context);
    }

    private boolean hasAuthorization(Authentication authentication, String authority) {
        boolean isAuthorized = false;
        Set<String> authorities = new HashSet<>(Collections.singleton(authority));
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (authorities.contains(grantedAuthority.getAuthority())) {
                isAuthorized = true;
            }
        }
        return authentication != null && authentication.isAuthenticated() && isAuthorized;
    }
}
