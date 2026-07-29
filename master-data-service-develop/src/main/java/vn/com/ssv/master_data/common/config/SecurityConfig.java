package vn.com.ssv.master_data.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.com.ssv.master_data.common.security.KeycloakJwtAuthenticationConverter;
import vn.com.ssv.master_data.common.security.RestSecurityExceptionHandler;

import java.util.List;

// SecurityConfig — cấu hình bảo mật tầng FILTER.
// Mọi request đi qua chuỗi filter này TRƯỚC khi tới controller:
// verify JWT -> dựng Authentication (kèm roles/permission) -> quyết định cho qua hay chặn.
@Configuration               // class định nghĩa bean
@EnableWebSecurity           // bật Spring Security web -> cho phép tự định nghĩa SecurityFilterChain
@EnableMethodSecurity        // bật @PreAuthorize / @PostAuthorize (không có -> các annotation đó bị bỏ qua)
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;
    private final RestSecurityExceptionHandler restSecurityExceptionHandler;

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/actuator/health/**",
            "/actuator/info",
            "/actuator/prometheus",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/organizations/**"
    };

    // Bean định nghĩa chuỗi filter bảo mật cho toàn ứng dụng.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF: REST stateless dùng Bearer token (không cookie) -> CSRF không áp dụng.
                .csrf(AbstractHttpConfigurer::disable)
                // Bật CORS: Spring Security tự tìm bean CorsConfigurationSource (theo type) bên dưới.
                .cors(Customizer.withDefaults())
                // Không tạo/dùng HTTP session: mỗi request tự mang token -> stateless, scale ngang dễ.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
        // Rule phân quyền theo URL: khai báo cụ thể trước, anyRequest() để cuối làm mặc định.
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                // Resource Server verify JWT (jwk Keycloak) + convert authorities.
                // Gắn entryPoint/accessDeniedHandler -> 401/403 ở tầng filter cũng trả ApiResponse.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
                        .authenticationEntryPoint(restSecurityExceptionHandler)
                        .accessDeniedHandler(restSecurityExceptionHandler))
                // Bắt cả lỗi auth không thuộc resource server (vd chưa có token tới anyRequest()) để globalEx trả res.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restSecurityExceptionHandler)
                        .accessDeniedHandler(restSecurityExceptionHandler));

        return http.build();
    }

    // Khai báo CORS: origin/method/header nào được phép gọi cross-origin.
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of(
//                "http://localhost:4200",
//                "https://platform.smartsolutionvn.com.vn"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:4203",
                "https://platform.smartsolutionvn.com.vn"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
