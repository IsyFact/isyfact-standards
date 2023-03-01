package de.bund.bva.isyfact.security.authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.security.xmlparser.RolePrivilegeMapper;

/**
 * Based on {@link org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter} but with additional
 * mapping from roles to privileges and a custom authority prefix to indicate the mapped privileges..
 */
public class RolePrivilegeGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(RolePrivilegeMapper.class);

    /** Authority prefix to use for all mapped authorities. */
    public static final String AUTHORITY_PREFIX = "PRIV_";

    /** Well known authorities claim names to check in order if no {@link #authoritiesClaimName} is set. */
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");

    /** The claim name to check for authorities. */
    private String authoritiesClaimName;

    /** Mapper from roles to privileges. */
    private final RolePrivilegeMapper rolePrivilegeMapper;

    public RolePrivilegeGrantedAuthoritiesConverter(RolePrivilegeMapper rolePrivilegeMapper) {
        this.rolePrivilegeMapper = rolePrivilegeMapper;
    }

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String authority : getAuthorities(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(AUTHORITY_PREFIX + authority));
        }

        return grantedAuthorities;
    }

    public void setRolesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getRolesClaimName(jwt);
        if (claimName == null) {
            LOG.trace("Returning no authorities since could not find any claims that might contain roles");
            return Collections.emptyList();
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Looking for roles in claim {}", claimName);
        }
        Object rolesClaim = jwt.getClaim(claimName);
        if (rolesClaim instanceof String) {
            if (StringUtils.hasText((String) rolesClaim)) {
                return Arrays.asList(((String) rolesClaim).split(" "));
            }
            return Collections.emptyList();
        }
        if (rolesClaim instanceof Collection) {
            return mapToPrivileges((Collection<String>) rolesClaim);
        }
        return Collections.emptyList();
    }

    private String getRolesClaimName(Jwt jwt) {
        if (authoritiesClaimName != null) {
            return authoritiesClaimName;
        }
        for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.hasClaim(claimName)) {
                return claimName;
            }
        }
        return null;
    }

    private Set<String> mapToPrivileges(Collection<String> roles) {
        return new HashSet<>(rolePrivilegeMapper.getPrivilegesByRoles(roles));
    }

}
