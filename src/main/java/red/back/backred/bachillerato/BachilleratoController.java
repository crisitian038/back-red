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

    // 🔹 CREAR
    @PostMapping
    public Bachillerato crear(@RequestBody Bachillerato bachillerato) {
        return service.crear(bachillerato);
    }

    // 🔹 GET TODOS O POR ESTADO (OPCIONAL)
    @GetMapping
    public Page<Bachillerato> listar(
            @RequestParam(required = false) EstadoBachillerato estado,
            Pageable pageable
    ) {
        return service.listarOpcional(estado, pageable);
    }

    // 🔹 CAMBIAR ESTADO
    @PatchMapping("/{id}/estado")
    public Bachillerato cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoBachillerato estado
    ) {
        return service.cambiarEstado(id, estado);
    }
}
