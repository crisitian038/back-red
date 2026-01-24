package red.back.backred.usuarios;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public Usuario crear(Usuario usuario) {
        // Convertir rol de String a Enum si es necesario
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USER);
        } else {
            // Si el rol viene como String, convertirlo a Enum
            try {
                if (usuario.getRol() != null) {
                    String rolStr = usuario.getRol().toString();
                    usuario.setRol(Rol.fromString(rolStr));
                }
            } catch (Exception e) {
                usuario.setRol(Rol.USER); // default
            }
        }
        
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        
        System.out.println("✅ Creando usuario: " + usuario.getEmail() + " con rol: " + usuario.getRol());
        
        return repo.save(usuario);
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Actualizar username
        if (usuarioActualizado.getUsername() != null && !usuarioActualizado.getUsername().isBlank()) {
            usuario.setUsername(usuarioActualizado.getUsername());
        }
        
        // Actualizar email
        if (usuarioActualizado.getEmail() != null && !usuarioActualizado.getEmail().isBlank()) {
            usuario.setEmail(usuarioActualizado.getEmail());
        }
        
        // Actualizar password solo si se proporciona
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isBlank()) {
            usuario.setPassword(encoder.encode(usuarioActualizado.getPassword()));
        }
        
        // Actualizar rol
        if (usuarioActualizado.getRol() != null) {
            try {
                String rolStr = usuarioActualizado.getRol().toString();
                usuario.setRol(Rol.fromString(rolStr));
            } catch (Exception e) {
                // Mantener el rol anterior si hay error
            }
        }
        
        System.out.println("✏️ Actualizando usuario: " + usuario.getEmail() + " con rol: " + usuario.getRol());
        
        return repo.save(usuario);
    }

    public void desactivar(Long id) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        repo.save(usuario);
    }

    public void activar(Long id) {
        Usuario u = repo.findById(id)
                .orElseThrow();
        u.setActivo(true);
        repo.save(u);
    }


    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
