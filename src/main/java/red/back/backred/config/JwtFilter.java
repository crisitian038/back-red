package red.back.backred.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Ignorar OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Ignorar rutas públicas (opcional, pero recomendado)
        String path = request.getRequestURI();
        if (path.startsWith("/auth/") ||
                path.startsWith("/uploads/") ||
                (path.startsWith("/ingles-inscripciones") && "POST".equals(request.getMethod())) ||
                (path.startsWith("/bachillerato-286-inscripciones") && "POST".equals(request.getMethod())) ||
                (path.startsWith("/carreras-ejecutivas-inscripciones") && "POST".equals(request.getMethod())) ||
                (path.startsWith("/contactos") && "POST".equals(request.getMethod())) ||
                (path.startsWith("/inscripciones") && "POST".equals(request.getMethod()))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token recibido: " + token);
            System.out.println("¿Token válido?: " + jwtUtil.isTokenValid(token));


            try {
                if (jwtUtil.isTokenValid(token)) {
                    String email = jwtUtil.extractEmail(token);
                    String rol = jwtUtil.extractRol(token);

                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    authorities
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                System.out.println("❌ Error JWT: " + e.getMessage());
                // No establecer autenticación
            }
        }

        filterChain.doFilter(request, response);
    }
}