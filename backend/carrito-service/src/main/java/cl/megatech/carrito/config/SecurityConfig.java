package cl.megatech.carrito.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cl.megatech.carrito.security.JwtAudienceValidator;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Value("${AZURE_ISSUER_URI}")
    private String azureIssuerUri;

    @Value("${AZURE_AUDIENCE}")
    private String azureAudience;

    @Value("${LOCAL_JWT_SECRET:megatech-comics-local-dev-secret-cambiar-en-produccion-32chars}")
    private String localJwtSecret;

    @Value("${LOCAL_JWT_ISSUER:https://megatech-comics-local}")
    private String localJwtIssuer;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UrlBasedCorsConfigurationSource corsConfigurationSource) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.authenticationManagerResolver(authenticationManagerResolver())
            );

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173"
        ));

        configuration.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Decoder para JWT de Azure AD (staff), validado con issuer + audience.
     */
    @Bean
    public JwtDecoder azureJwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(azureIssuerUri).build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(azureIssuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtAudienceValidator(azureAudience);

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));

        return decoder;
    }

    /**
     * Decoder para JWT locales emitidos por bff-service a clientes propios
     * (email + contraseña). Firmados con HS256 usando la misma clave
     * compartida que LocalJwtService en bff-service.
     */
    @Bean
    public JwtDecoder localJwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
                localJwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(localJwtIssuer));

        return decoder;
    }

    /**
     * Resuelve entre los dos AuthenticationManager según el issuer del
     * token: Azure AD (staff) o el local (clientes propios).
     */
    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        JwtAuthenticationProvider azureProvider = new JwtAuthenticationProvider(azureJwtDecoder());
        JwtAuthenticationProvider localProvider = new JwtAuthenticationProvider(localJwtDecoder());

        AuthenticationManager azureManager = azureProvider::authenticate;
        AuthenticationManager localManager = localProvider::authenticate;

        Map<String, AuthenticationManager> managersByIssuer = Map.of(
                azureIssuerUri, azureManager,
                localJwtIssuer, localManager
        );

        return new JwtIssuerAuthenticationManagerResolver(managersByIssuer::get);
    }
}
