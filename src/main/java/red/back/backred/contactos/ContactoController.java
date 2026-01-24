package red.back.backred.contactos;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contactos")
@RequiredArgsConstructor
public class ContactoController {

    private final ContactoService service;

    // PUBLICO (FORMULARIO WEB)
    @PostMapping
    public Contacto crear(@RequestBody Contacto contacto) {
        return service.crearContacto(contacto);
    }

    // PANEL
    @GetMapping
    public Page<Contacto> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listarTodos(page, size);
    }

    @GetMapping("/estado/{estado}")
    public Page<Contacto> listarPorEstado(
            @PathVariable EstadoContacto estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.listarPorEstado(estado, page, size);
    }

    @PutMapping("/{id}/estado/{estado}")
    public Contacto cambiarEstado(
            @PathVariable Long id,
            @PathVariable EstadoContacto estado) {

        return service.cambiarEstado(id, estado);
    }
}
