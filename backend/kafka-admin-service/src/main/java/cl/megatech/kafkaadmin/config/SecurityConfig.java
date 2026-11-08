package cl.megatech.kafkaadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Protege solo las operaciones destructivas/sensibles de administración:
// eliminar tópicos, eliminar consumer groups y modificar configuración de
// tópicos. El resto de la API (lecturas, crear tópico, aumentar
// particiones) queda abierta, igual que hoy la consume el equipo.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        HttpMethod.DELETE, "/api/kafka/topics/**"
                ).authenticated()
                .requestMatchers(
                        HttpMethod.DELETE, "/api/kafka/consumer-groups/**"
                ).authenticated()
                .requestMatchers(
                        HttpMethod.PUT, "/api/kafka/topics/*/config"
                ).authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );

        return http.build();
    }
}
