package com.megatech.bffservice.config;

import com.megatech.bffservice.exception.GlobalExceptionHandler;
import com.megatech.bffservice.security.JwtAudienceValidator;
import com.megatech.bffservice.security.LocalJwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GlobalExceptionHandler globalExceptionHandler;
    private final LocalJwtService localJwtService;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.security.jwt.expected-audience}")
    private String expectedAudience;

    public SecurityConfig(GlobalExceptionHandler globalExceptionHandler, LocalJwtService localJwtService) {
        this.globalExceptionHandler = globalExceptionHandler;
        this.localJwtService = localJwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

        // Registro/login de clientes propios (email + contraseña) es público
        .requestMatchers("/api/auth/clientes/**").permitAll()

        // El resto de las APIs requiere autenticación
        .requestMatchers("/api/**").authenticated()

        // Cualquier otra ruta también requiere autenticación
        .anyRequest().authenticated()
)

                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(authenticationManagerResolver())
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(globalExceptionHandler)
                        .accessDeniedHandler(globalExceptionHandler)
                );

        return http.build();
    }

    /**
     * Orígenes permitidos para llamadas cross-origin desde el frontend.
     * Lista explícita (no wildcard) para poder agregar más orígenes
     * cuando el frontend se despliegue.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
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

    /**
     * Decoder para los JWT locales emitidos a clientes propios (email + contraseña).
     * Firmados con HS256 usando la clave compartida de LocalJwtService.
     */
    @Bean
    public JwtDecoder localJwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(localJwtService.getSecretKey())
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(localJwtService.getIssuer()));
        return decoder;
    }

    /**
     * Resuelve entre dos AuthenticationManager según el issuer del token:
     * el de Azure AD (staff) y el local (clientes propios). Ambos flujos
     * coexisten sin interferirse.
     */
    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        JwtAuthenticationProvider azureProvider = new JwtAuthenticationProvider(jwtDecoder());
        azureProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter());

        JwtAuthenticationProvider localProvider = new JwtAuthenticationProvider(localJwtDecoder());

        AuthenticationManager azureManager = azureProvider::authenticate;
        AuthenticationManager localManager = localProvider::authenticate;

        Map<String, AuthenticationManager> managersByIssuer = Map.of(
                issuerUri, azureManager,
                localJwtService.getIssuer(), localManager
        );

        return new JwtIssuerAuthenticationManagerResolver(managersByIssuer::get);
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