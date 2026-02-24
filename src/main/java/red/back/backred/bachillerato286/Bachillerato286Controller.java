package red.back.backred.bachillerato286;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.util.List;

@RestController
@RequestMapping("/bachillerato-286-inscripciones")
@RequiredArgsConstructor
public class Bachillerato286Controller {

    private final Bachillerato286Service service;

    @PostMapping
    public Bachillerato286Inscripcion crear(@RequestBody Bachillerato286Inscripcion inscripcion) {
        return service.crear(inscripcion);
    }

    @GetMapping
    public List<Bachillerato286Inscripcion> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public Bachillerato286Inscripcion obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @PathVariable String estado) {
        service.cambiarEstado(id, EstadoInscripcion.valueOf(estado));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
