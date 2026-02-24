package red.back.backred.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; 

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hora de cache para preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ========================================
                        // 🟢 PÚBLICOS - SIN AUTENTICACIÓN
                        // ========================================
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // Carreras - lectura pública
                        .requestMatchers(HttpMethod.GET, "/carreras").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carreras/**").permitAll()

                        // Carreras ejecutivas - lectura pública
                        .requestMatchers(HttpMethod.GET, "/carreras-ejecutivas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carreras-ejecutivas/**").permitAll()
                        
                        // Noticias - lectura pública de publicadas, GET por ID público, escritura protegida
                        .requestMatchers(HttpMethod.GET, "/api/noticias/publicadas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/noticias/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/noticias").authenticated() // Solo admins
                        .requestMatchers(HttpMethod.POST, "/api/noticias").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/noticias/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/noticias/**").authenticated()
                        
                        // Inscripciones - creación pública, gestión protegida
                        .requestMatchers(HttpMethod.POST, "/inscripciones").permitAll()
                        
                        // Nuevas Inscripciones por programa - creación pública, lectura/gestión protegida
                        .requestMatchers(HttpMethod.POST, "/ingles-inscripciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ingles-inscripciones").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/ingles-inscripciones/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/ingles-inscripciones/**").authenticated()


                        .requestMatchers(HttpMethod.GET, "/carreras-ejecutivas-inscripciones").permitAll()
                        .requestMatchers(HttpMethod.POST, "/carreras-ejecutivas-inscripciones").authenticated()


                        .requestMatchers(HttpMethod.POST, "/bachillerato-286-inscripciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/bachillerato-286-inscripciones").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/bachillerato-286-inscripciones/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/bachillerato-286-inscripciones/**").authenticated()
                        
                        .requestMatchers(HttpMethod.POST, "/carreras-ejecutivas-inscripciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carreras-ejecutivas-inscripciones").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/carreras-ejecutivas-inscripciones/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/carreras-ejecutivas-inscripciones/**").authenticated()
                        
                        // Contactos - creación pública, gestión protegida
                        .requestMatchers(HttpMethod.POST, "/contactos").permitAll()
                        
                        // Bachilleratos - GET públicos (todos y con parámetros)
                        .requestMatchers(HttpMethod.GET, "/api/bachillerato").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bachillerato/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bachillerato").permitAll()

                        // ========================================
                        // 🔐 PROTEGIDOS - REQUIEREN AUTENTICACIÓN
                        // ========================================

                        // Usuarios - todo requiere autenticación
                        .requestMatchers(HttpMethod.GET, "/usuarios").authenticated()
                        .requestMatchers(HttpMethod.GET, "/usuarios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/usuarios").authenticated()
                        .requestMatchers(HttpMethod.POST, "/usuarios/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/usuarios").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/usuarios/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/usuarios").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").authenticated()

                        // Inscripciones - lectura y edición protegida
                        .requestMatchers(HttpMethod.GET, "/inscripciones").authenticated()
                        .requestMatchers(HttpMethod.GET, "/inscripciones/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/inscripciones").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/inscripciones/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/inscripciones").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/inscripciones/**").authenticated()

                        // Contactos - lectura y edición protegida
                        .requestMatchers(HttpMethod.GET, "/contactos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/contactos/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/contactos").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/contactos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/contactos").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/contactos/**").authenticated()

                        // Bachilleratos - edición protegida
                        .requestMatchers(HttpMethod.PUT, "/api/bachillerato").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/bachillerato/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/bachillerato").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/bachillerato/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/bachillerato").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/bachillerato/**").authenticated()

                        // Lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Aquí inyectamos tu filtro personalizado ANTES del filtro de autenticación por defecto
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean necesario para que AuthController pueda hacer el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean para encriptar contraseñas en la base de datos
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}