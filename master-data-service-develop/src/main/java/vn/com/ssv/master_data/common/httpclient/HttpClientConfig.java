package vn.com.ssv.master_data.common.httpclient;

import io.micrometer.observation.ObservationRegistry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;
import vn.com.ssv.master_data.common.util.SecurityUtil;

// AUTO-đăng ký @HttpExchange interface thành proxy bean, NHÓM theo base URL.
//  1 group = 1 base URL. Client chung URL -> để chung 1 SUB-PACKAGE -> cùng group.
//   internal/  -> group "internal" (gateway nội bộ)
//   (thêm)     -> @ImportHttpServices(group="external", basePackages="...feature.external") cho URL khác
// Cấu hình chung (token + traceId) khai 1 lần; base URL set RIÊNG theo tên group.
@Configuration
@ImportHttpServices(group = "internal", basePackages = "vn.com.ssv.master_data.feature.client")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HttpClientConfig {

    static String INTERNAL_REGISTRATION_ID = "internal";

    InternalProperties internalProperties;

    @Bean
    public RestClientHttpServiceGroupConfigurer httpServiceGroupConfigurer(
            ObservationRegistry observationRegistry,
            OAuth2AuthorizedClientManager authorizedClientManager) {
        return groups -> groups.forEachClient((group, builder) -> {
            // CHUNG mọi group
            builder.observationRegistry(observationRegistry);                       // traceId propagate
            builder.requestInterceptor(authInterceptor(authorizedClientManager));   // token user / client_credentials
            // base URL RIÊNG theo group
            String baseUrl = switch (group.name()) {
                case "internal" -> internalProperties.getBaseUrl();
                // case "external" -> externalProperties.getBaseUrl();
                default -> null;
            };
            if (StringUtils.isNotBlank(baseUrl)) {
                builder.baseUrl(baseUrl);
            }
        });
    }

    // Manager lấy token client_credentials (luồng KHÔNG có user: job/kafka/startup).
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    // Gắn Bearer vào mọi request đi ra: ưu tiên token user; không có -> token client_credentials của service.
    private ClientHttpRequestInterceptor authInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return (request, body, execution) -> {
            String token = SecurityUtil.getTokenValue();
            if (StringUtils.isBlank(token)) {
                token = clientCredentialsToken(authorizedClientManager);
            }
            if (StringUtils.isNotBlank(token)) {
                request.getHeaders().setBearerAuth(token);
            }
            return execution.execute(request, body);
        };
    }

    private String clientCredentialsToken(OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(INTERNAL_REGISTRATION_ID)
                .principal(INTERNAL_REGISTRATION_ID)
                .build();
        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);
        return client != null ? client.getAccessToken().getTokenValue() : null;
    }
}
