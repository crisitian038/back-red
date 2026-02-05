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
        System.out.println("📥 POST /carreras - Creando carrera");
        System.out.println("   Nombre: " + carrera.getNombre());
        System.out.println("   Activa: " + carrera.isActiva());
        return service.crear(carrera);
    }

    @GetMapping("/admin")
    public List<Carrera> listarTodas() {
        System.out.println("📋 GET /carreras/admin - Listando todas");
        return service.listarTodas();
    }

    @PutMapping("/{id}")
    public Carrera actualizar(
            @PathVariable Long id,
            @RequestBody Carrera carrera
    ) {
        System.out.println("📥 PUT /carreras/" + id + " - Actualizando");
        System.out.println("   Nombre: " + carrera.getNombre());
        System.out.println("   Activa: " + carrera.isActiva());
        System.out.println("   Modalidad: " + carrera.getModalidad());
        System.out.println("   Descripción: " +
                (carrera.getDescripcion() != null ?
                        carrera.getDescripcion().substring(0, Math.min(50, carrera.getDescripcion().length())) + "..." : "null"));

        return service.actualizar(id, carrera);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activa
    ) {
        System.out.println("🔄 PATCH /carreras/" + id + "/estado?activa=" + activa);
        service.cambiarEstado(id, activa);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        System.out.println("🗑️ DELETE /carreras/" + id);
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }

    /* ======================
       PÚBLICO
       ====================== */

    @GetMapping
    public List<Carrera> activas() {
        System.out.println("🌐 GET /carreras - Listando activas");
        return service.listarActivas();
    }

    @GetMapping("/top")
    public List<Carrera> top3() {
        System.out.println("🏆 GET /carreras/top - Top 3");
        return service.top3();
    }
}