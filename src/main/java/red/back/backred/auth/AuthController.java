package red.back.backred.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import red.back.backred.config.JwtUtil;
import red.back.backred.usuarios.Usuario;
import red.back.backred.usuarios.UsuarioRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        return jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }
}
