package red.back.backred.carreras;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras")
@RequiredArgsConstructor
public class CarreraController {

    private final CarreraService service;

    /* ======================
       ADMIN
       ====================== */

    @PostMapping
    public Carrera crear(@RequestBody Carrera carrera) {
        return service.crear(carrera);
    }

    @GetMapping("/admin")
    public List<Carrera> listarTodas() {
        return service.listarTodas();
    }

    @PutMapping("/{id}")
    public Carrera actualizar(
            @PathVariable Long id,
            @RequestBody Carrera carrera
    ) {
        return service.actualizar(id, carrera);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activa
    ) {
        service.cambiarEstado(id, activa);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }

    /* ======================
       PÚBLICO
       ====================== */

    @GetMapping
    public List<Carrera> activas() {
        return service.listarActivas();
    }

    @GetMapping("/top")
    public List<Carrera> top3() {
        return service.top3();
    }
}
