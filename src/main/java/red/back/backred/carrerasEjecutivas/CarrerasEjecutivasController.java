package red.back.backred.carrerasEjecutivas;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.util.List;

@RestController
@RequestMapping("/carreras-ejecutivas-inscripciones")
@RequiredArgsConstructor
public class CarrerasEjecutivasController {

    private final CarrerasEjecutivasService service;

    @PostMapping
    public CarrerasEjecutivasInscripcion crear(@RequestBody CarrerasEjecutivasInscripcion inscripcion) {
        return service.crear(inscripcion);
    }

    @GetMapping
    public List<CarrerasEjecutivasInscripcion> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public CarrerasEjecutivasInscripcion obtenerPorId(@PathVariable Long id) {
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
