package red.back.backred.carrerasEjecutivas;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras-ejecutivas")
@RequiredArgsConstructor
public class CarreraEjecutivaController {

    private final CarreraEjecutivaService service;

    @PostMapping
    public CarreraEjecutiva crear(@RequestBody CarreraEjecutiva c) {
        return service.crear(c);
    }

    @GetMapping("/admin")
    public List<CarreraEjecutiva> listarTodas() {
        return service.listarTodas();
    }

    @PutMapping("/{id}")
    public CarreraEjecutiva actualizar(@PathVariable Long id, @RequestBody CarreraEjecutiva c) {
        return service.actualizar(id, c);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam boolean activa) {
        service.cambiarEstado(id, activa);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<CarreraEjecutiva> activas() {
        return service.listarActivas();
    }

    @GetMapping("/top")
    public List<CarreraEjecutiva> top3() {
        return service.top3();
    }
}
