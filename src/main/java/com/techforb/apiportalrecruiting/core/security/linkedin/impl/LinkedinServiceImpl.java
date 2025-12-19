package com.techforb.apiportalrecruiting.core.security.linkedin.impl;

import com.techforb.apiportalrecruiting.core.config.LinkedInConfig;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.security.linkedin.LinkedInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkedinServiceImpl implements LinkedInService {

    private final LinkedInConfig linkedInConfig;
    private final RestTemplate restTemplate;
    private final LocalizedMessageService localizedMessageService;

    HttpHeaders formHeaders;
    HttpHeaders jsonHeaders;

    private static final String LINKEDIN_AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String LINKEDIN_TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String LINKEDIN_USERINFO_URL = "https://api.linkedin.com/v2/userinfo";

    @PostConstruct
    void initHeaders() {
        this.formHeaders = new HttpHeaders();
        this.formHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        this.jsonHeaders = new HttpHeaders();
        this.jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public String getAuthorizationUrl(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException(localizedMessageService.getMessage("auth.linkedin.state_required"));
        }

        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                LINKEDIN_AUTH_URL,
                linkedInConfig.getClientId(),
                linkedInConfig.getRedirectUri(),
                linkedInConfig.getScope().replace(" ", "%20"),
                state);
    }

    @Override
    public String getAccessToken(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException(localizedMessageService.getMessage("auth.linkedin.code_required"));
        }

        try {
            log.info("Iniciando obtención de token de LinkedIn con código: {}", code);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", linkedInConfig.getRedirectUri());
            body.add("client_id", linkedInConfig.getClientId());
            body.add("client_secret", linkedInConfig.getClientSecret());

            log.info("Enviando request a LinkedIn con redirect_uri: {}", linkedInConfig.getRedirectUri());
            log.info("Client ID: {}", linkedInConfig.getClientId());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, formHeaders);

            ResponseEntity<Map> response = restTemplate.postForEntity(LINKEDIN_TOKEN_URL, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            log.info("Response de LinkedIn: {}", responseBody);

            if (responseBody == null || !responseBody.containsKey("access_token")) {
                log.error("Response de LinkedIn no contiene access_token: {}", responseBody);
                throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.token_error", responseBody));
            }

            String accessToken = (String) responseBody.get("access_token");
            log.info("Token de acceso obtenido exitosamente");
            return accessToken;

        } catch (RestClientException e) {
            log.error("Error de conectividad obteniendo token de LinkedIn: {}", e.getMessage(), e);
            throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.token_fetch_error", e.getMessage()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado obteniendo token de LinkedIn: {}", e.getMessage(), e);
            throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.token_fetch_error", e.getMessage()));
        }
    }

    @Override
    public Map<String, Object> getUserProfile(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException(localizedMessageService.getMessage("auth.linkedin.token_required"));
        }

        try {
            HttpHeaders bearerHeaders = new HttpHeaders();
            bearerHeaders.addAll(jsonHeaders);
            bearerHeaders.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(bearerHeaders);

            ResponseEntity<Map> response = restTemplate.exchange(
                    LINKEDIN_USERINFO_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> userInfo = response.getBody();
            log.info("UserInfo obtenido de LinkedIn: {}", userInfo);

            return userInfo;
        } catch (RestClientException e) {
            log.error("Error de conectividad obteniendo perfil de LinkedIn: {}", e.getMessage(), e);
            throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.profile_error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado obteniendo perfil de LinkedIn: {}", e.getMessage(), e);
            throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.profile_error", e.getMessage()));
        }
    }

    @Override
    public String getUserEmail(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException(localizedMessageService.getMessage("auth.linkedin.token_required"));
        }

        try {
            Map<String, Object> userInfo = getUserProfile(accessToken);

            if (userInfo == null || !userInfo.containsKey("email")) {
                log.error("No se encontró email en userInfo: {}", userInfo);
                throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.email_not_found"));
            }

            String email = (String) userInfo.get("email");
            log.info("Email obtenido de LinkedIn: {}", email);
            return email;

        } catch (RuntimeException e) {
            String emailNotFoundMessage = localizedMessageService.getMessage("auth.linkedin.email_not_found");
            if (e.getMessage() != null && e.getMessage().equals(emailNotFoundMessage)) {
                throw e;
            }
            log.error("Error obteniendo email de LinkedIn: {}", e.getMessage(), e);
            throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.email_fetch_error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado obteniendo email de LinkedIn: {}", e.getMessage(), e);
            throw new RuntimeException(localizedMessageService.getMessage("auth.linkedin.email_fetch_error", e.getMessage()));
        }
    }
}