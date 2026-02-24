package red.back.backred.ingles;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import red.back.backred.ingles.EstadoInscripcionIngl;

import java.util.List;

@RestController
@RequestMapping("/ingles-inscripciones")
@RequiredArgsConstructor
public class InglesController {

    private final InglesService service;

    @PostMapping
    public InglesPrograma crear(@RequestBody InglesPrograma inscripcion) {
        return service.crear(inscripcion);
    }

    @GetMapping
    public List<InglesPrograma> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public InglesPrograma obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @PathVariable EstadoInscripcionIngl estado) {
        service.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }
}

