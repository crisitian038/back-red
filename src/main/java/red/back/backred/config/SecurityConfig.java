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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // Configuración de CORS para conectar con React
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Públicos: Autenticación y OPTIONS (preflight de CORS)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Carreras (lectura)
                        .requestMatchers(HttpMethod.GET, "/carreras/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/bachillerato").permitAll()


                        // 📰 Noticias públicas
                        .requestMatchers(HttpMethod.GET, "/api/noticias/publicadas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/noticias/{id}").permitAll()

                        // Formularios públicos
                        .requestMatchers(HttpMethod.POST, "/inscripciones").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bachillerato").permitAll()
                        .requestMatchers(HttpMethod.POST, "/contactos").permitAll()

                        // ===============================
                        // 🔐 PROTEGIDOS (ADMIN)
                        // ===============================
                        .requestMatchers("/api/noticias/**").authenticated()
                        .requestMatchers("/usuarios/**").authenticated()
                        .requestMatchers("/inscripciones/**").authenticated()
                        .requestMatchers("/contactos/**").authenticated()

                        .anyRequest().authenticated()
                )

                // Aquí inyectamos tu filtro personalizado antes del filtro por defecto
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