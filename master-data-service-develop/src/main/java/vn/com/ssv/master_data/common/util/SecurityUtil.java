package vn.com.ssv.master_data.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

import static vn.com.ssv.master_data.common.util.Const.*;

@UtilityClass
public final class SecurityUtil {
    public static Optional<Jwt> getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }
        return Optional.empty();
    }

    public static String getCurrentUsername() {
        return getJwt().map(jwt -> jwt.getClaimAsString(PREFERRED_USERNAME)).orElse(ANONYMOUS);
    }

    public static Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        if (getJwt().isPresent()) {
            roles = getRolesFromJwt(getJwt().get());
        }
        return roles;
    }

    public static Set<String> getRolesFromJwt(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        if (jwt != null) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
            if (realmAccess != null) {
                Object rolesObj = realmAccess.get(ROLES);

                if (rolesObj instanceof List<?>) {
                    roles = ((List<?>) rolesObj).stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .collect(Collectors.toSet());
                }
            }
        }
        return roles;
    }

    public static Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            permissions = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                    .filter(auth -> auth.contains(":"))
                    .collect(Collectors.toSet());
        }
        return permissions;
    }

    public static String getTokenValue() {
       return getJwt().map(Jwt::getTokenValue).orElse(null);
    }

    public static boolean isAuthenticated() {
        return getJwt().isPresent();
    }
}
