package red.back.backred.inscripciones;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService service;

    @PostMapping
    public Inscripcion crear(@RequestBody Inscripcion inscripcion) {
        return service.crear(inscripcion);
    }

    @GetMapping
    public Page<Inscripcion> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listar(PageRequest.of(page, size, Sort.by("fechaRegistro").descending()));
    }

    @GetMapping("/estado/{estado}")
    public Page<Inscripcion> listarPorEstado(
            @PathVariable EstadoInscripcion estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listarPorEstado(
                estado,
                PageRequest.of(page, size, Sort.by("fechaRegistro").descending())
        );
    }

    @PutMapping("/{id}/estado/{estado}")
    public Inscripcion cambiarEstado(
            @PathVariable Long id,
            @PathVariable EstadoInscripcion estado) {

        return service.cambiarEstado(id, estado);
    }
}
