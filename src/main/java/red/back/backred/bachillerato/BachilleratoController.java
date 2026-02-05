package red.back.backred.bachillerato;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bachillerato")
@RequiredArgsConstructor
@CrossOrigin
public class BachilleratoController {

    private final BachilleratoService service;

    // 🔹 CREAR INSCRIPCIÓN (PÚBLICO)
    @PostMapping
    public Bachillerato crear(@RequestBody Bachillerato bachillerato) {
        System.out.println("📥 POST /api/bachillerato - Inscripción pública recibida");
        System.out.println("   Datos recibidos:");
        System.out.println("     - nombreCompleto: " + bachillerato.getNombreCompleto());
        System.out.println("     - email: " + bachillerato.getEmail());
        System.out.println("     - telefono: " + bachillerato.getTelefono());
        System.out.println("     - curp: " + bachillerato.getCurp());
        System.out.println("     - fechaNacimiento: " + bachillerato.getFechaNacimiento());


        return service.crear(bachillerato);
    }

    // 🔹 GET TODOS O POR ESTADO (OPCIONAL)
    @GetMapping
    public Page<Bachillerato> listar(
            @RequestParam(required = false) EstadoBachillerato estado,
            Pageable pageable
    ) {
        System.out.println("📋 GET /api/bachillerato - Estado filtro: " + estado);
        return service.listarOpcional(estado, pageable);
    }

    // 🔹 CAMBIAR ESTADO (ADMIN)
    @PatchMapping("/{id}/estado")
    public Bachillerato cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoBachillerato estado
    ) {
        System.out.println("🔄 PATCH /api/bachillerato/" + id + "/estado - Nuevo estado: " + estado);
        return service.cambiarEstado(id, estado);
    }
}