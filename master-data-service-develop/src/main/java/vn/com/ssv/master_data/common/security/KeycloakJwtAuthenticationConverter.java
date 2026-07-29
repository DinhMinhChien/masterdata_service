package vn.com.ssv.master_data.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static vn.com.ssv.master_data.common.util.Const.ROLE_PREFIX;
import static vn.com.ssv.master_data.common.util.SecurityUtil.getRolesFromJwt;

// Lấy danh sách role từ claim , danh sách permission theo role để add vào authorities
@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final PermissionResolver permissionResolver;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Set<String> roles = getRolesFromJwt(jwt);
        //thêm prefix vì @PreAuthorize(hasRole...) nó tự thêm tiền tố
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
        permissionResolver.permissionsOf(roles)
                .forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        return new JwtAuthenticationToken(jwt, authorities);

    }
}
