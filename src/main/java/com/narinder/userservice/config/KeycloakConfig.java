package com.narinder.userservice.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.adminClientId}")
    private String clientId;

    @Value("${app.keycloak.adminClientSecret}")
    private String clientSecret;

    @Value("${app.keycloak.serverUrl}")
    private String serverUrl;

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .clientSecret(clientSecret)
                .clientId(clientId)
                .grantType("client_credentials")
                .realm(realm)
                .serverUrl(serverUrl)
                .build();
    }

}
