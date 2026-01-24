package red.back.backred.noticias;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/noticias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoticiaController {

    private final NoticiaService noticiaService;

    // 🔐 ADMIN

    @GetMapping
    public List<Noticia> listarTodas() {
        return noticiaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Noticia obtenerPorId(@PathVariable Long id) {
        return noticiaService.obtenerPorId(id);
    }

    @PostMapping
    public Noticia crear(@RequestBody Noticia noticia) {
        return noticiaService.guardar(noticia);
    }

    @PutMapping("/{id}")
    public Noticia actualizar(@PathVariable Long id, @RequestBody Noticia noticia) {
        noticia.setId(id);
        return noticiaService.guardar(noticia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        noticiaService.eliminar(id);
    }

    // 🌐 PUBLICO

    @GetMapping("/publicadas")
    public List<Noticia> listarPublicadas() {
        return noticiaService.obtenerPublicadas();
    }
}
