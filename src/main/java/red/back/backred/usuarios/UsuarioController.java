package red.back.backred.usuarios;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = service.crear(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear usuario: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listar() {
        return service.listar();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = service.actualizar(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar usuario: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public void activar(@PathVariable Long id) {
        service.activar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
