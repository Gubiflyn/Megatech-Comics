package com.megatech.bffservice.config;

import com.megatech.bffservice.exception.GlobalExceptionHandler;
import com.megatech.bffservice.security.JwtAudienceValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GlobalExceptionHandler globalExceptionHandler;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.security.jwt.expected-audience}")
    private String expectedAudience;

    public SecurityConfig(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
        // Health check público
        .requestMatchers("/actuator/health").permitAll()

        // Permitir preflight CORS sin autenticación
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Catálogo público para permitir navegación de la tienda
        .requestMatchers(
                HttpMethod.GET,
                "/api/catalogo",
                "/api/catalogo/**"
        ).permitAll()

        // El resto de las APIs requiere autenticación
        .requestMatchers("/api/**").authenticated()

        // Cualquier otra ruta también requiere autenticación
        .anyRequest().authenticated()
)
                
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(globalExceptionHandler)
                        .accessDeniedHandler(globalExceptionHandler)
                );

        return http.build();
    }

    /**
     * Configura la validación del JWT.
     * Se valida issuer, firma, expiración y audience.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtAudienceValidator(expectedAudience);

        jwtDecoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        issuerValidator,
                        audienceValidator
                )
        );

        return jwtDecoder;
    }

    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                this::extractAuthorities
        );

        return converter;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities =
                new ArrayList<>();

        List<String> roles =
                jwt.getClaimAsStringList("roles");

        if (roles != null) {
            roles.forEach(role ->
                    authorities.add(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role
                            )
                    )
            );
        }

        String scopes =
                jwt.getClaimAsString("scp");

        if (scopes != null && !scopes.isBlank()) {
            for (String scope : scopes.split(" ")) {
                authorities.add(
                        new SimpleGrantedAuthority(
                                "SCOPE_" + scope
                        )
                );
            }
        }

        return authorities;
    }
}