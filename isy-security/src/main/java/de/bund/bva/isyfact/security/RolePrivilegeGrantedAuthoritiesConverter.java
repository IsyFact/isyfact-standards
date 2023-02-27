package de.bund.bva.isyfact.security;

import de.bund.bva.isyfact.security.xmlparser.RolePrivilegeMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

public class RolePrivilegeGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final Log logger = LogFactory.getLog(this.getClass());
    private static final String DEFAULT_AUTHORITY_PREFIX = PrivilegeVoter.PRIVELEGE_PREFIX;
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;
    private String authoritiesClaimName;
    private RolePrivilegeMapper rolePrivilegeMapper;

    public RolePrivilegeGrantedAuthoritiesConverter(RolePrivilegeMapper mapper) {
        this.rolePrivilegeMapper = mapper;
    }

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String authority : this.getAuthorities(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(this.authorityPrefix + authority));
        }

        return grantedAuthorities;
    }

    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
    }

    public void setRolesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private String getRolesClaimName(Jwt jwt) {
        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        } else {
            Iterator<String> authIter = WELL_KNOWN_AUTHORITIES_CLAIM_NAMES.iterator();
            String claimName;
            do {
                if (!authIter.hasNext()) {
                    return null;
                }
                claimName = authIter.next();
            } while(!jwt.hasClaim(claimName));
            return claimName;
        }
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = this.getRolesClaimName(jwt);
        if (claimName == null) {
            this.logger.trace("Returning no authorities since could not find any claims that might contain roles");
            return Collections.emptyList();
        } else {
            this.logger.trace(LogMessage.format("Looking for roles in claim %s", claimName));
            Object rolesClaim = jwt.getClaim(claimName);
            if (rolesClaim instanceof String) {
                List<String> roles = StringUtils.hasText((String) rolesClaim) ? (Arrays.asList(((String) rolesClaim).split(" "))) : Collections.emptyList();
                return mapToPrivileges(roles);
            } else {
                List<String> roles = rolesClaim instanceof List ? ((List<String>) rolesClaim) : Collections.emptyList();
                return mapToPrivileges(roles);
            }
        }
    }

    private Set<String> mapToPrivileges(List<String> roles) {
        return new HashSet<>(rolePrivilegeMapper.getPrivilegesByRoles(roles));
    }
}
