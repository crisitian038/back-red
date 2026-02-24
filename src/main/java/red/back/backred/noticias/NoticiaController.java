package red.back.backred.noticias;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import red.back.backred.config.FileStorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/noticias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoticiaController {

    private final NoticiaService noticiaService;
    private final FileStorageService fileStorageService;

    // ================== ENDPOINTS PÚBLICOS (SIN AUTENTICACIÓN) ==================
    @GetMapping("/publicadas")
    public List<Noticia> listarPublicadas() {
        return noticiaService.obtenerPublicadas();
    }

    // ================== VERIFICAR ID PRIMERO ==================
    @GetMapping("/{id}")
    public Noticia obtenerPorId(@PathVariable Long id) {
        return noticiaService.obtenerPorId(id);
    }

    // ================== CREAR CON ARCHIVO O URL (REQUIERE AUTENTICACIÓN) ==================
    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    public Noticia crear(
            @RequestParam String titulo,
            @RequestParam(required = false) String descripcionCorta,
            @RequestParam(required = false) String introduccion,
            @RequestParam(required = false) String contenido,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(defaultValue = "true") Boolean publicada) throws IOException {
        
        System.out.println("📩 ========== CREAR NOTICIA ==========");
        System.out.println("Título: " + titulo);
        System.out.println("Descripción: " + descripcionCorta);
        System.out.println("Introducción: " + introduccion);
        System.out.println("Contenido: " + contenido);
        System.out.println("Publicada: " + publicada);
        System.out.println("imagenFile: " + (imagenFile != null ? imagenFile.getOriginalFilename() : "NULL"));
        System.out.println("imagenUrl: " + imagenUrl);
        
        Noticia noticia = new Noticia();
        noticia.setTitulo(titulo);
        noticia.setDescripcionCorta(descripcionCorta);
        noticia.setIntroduccion(introduccion);
        noticia.setContenido(contenido);
        noticia.setPublicada(publicada);

        // Procesar imagen: archivo o URL
        if (imagenFile != null && !imagenFile.isEmpty()) {
            System.out.println("💾 Guardando archivo...");
            String rutaImagen = fileStorageService.guardarImagen(imagenFile);
            System.out.println("✅ Archivo guardado en: " + rutaImagen);
            noticia.setImagen(rutaImagen);
        } else if (imagenUrl != null && !imagenUrl.isEmpty()) {
            System.out.println("🌐 Usando URL: " + imagenUrl);
            noticia.setImagen(imagenUrl);
        } else {
            System.out.println("⚠️ Sin imagen (ni archivo ni URL)");
        }

        System.out.println("💾 Guardando en BD...");
        Noticia resultado = noticiaService.guardar(noticia);
        System.out.println("✅ Noticia guardada con ID: " + resultado.getId());
        System.out.println("📩 ========== FIN CREATE ==========");
        
        return resultado;
    }

    // ================== ACTUALIZAR CON ARCHIVO O URL (REQUIERE AUTENTICACIÓN) ==================
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data", "application/json"})
    public Noticia actualizar(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam(required = false) String descripcionCorta,
            @RequestParam(required = false) String introduccion,
            @RequestParam(required = false) String contenido,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(defaultValue = "true") Boolean publicada) throws IOException {
        
        Noticia noticiaExistente = noticiaService.obtenerPorId(id);
        if (noticiaExistente == null) {
            throw new IllegalArgumentException("Noticia no encontrada con ID: " + id);
        }

        noticiaExistente.setTitulo(titulo);
        noticiaExistente.setDescripcionCorta(descripcionCorta);
        noticiaExistente.setIntroduccion(introduccion);
        noticiaExistente.setContenido(contenido);
        noticiaExistente.setPublicada(publicada);

        // Procesar imagen: si hay nuevo archivo, eliminar el viejo y guardar el nuevo
        if (imagenFile != null && !imagenFile.isEmpty()) {
            // Eliminar imagen anterior si es una ruta local
            if (noticiaExistente.getImagen() != null && !noticiaExistente.getImagen().startsWith("http")) {
                fileStorageService.eliminarImagen(noticiaExistente.getImagen());
            }
            String rutaImagen = fileStorageService.guardarImagen(imagenFile);
            noticiaExistente.setImagen(rutaImagen);
        } else if (imagenUrl != null && !imagenUrl.isEmpty()) {
            // Eliminar imagen anterior si es una ruta local
            if (noticiaExistente.getImagen() != null && !noticiaExistente.getImagen().startsWith("http")) {
                fileStorageService.eliminarImagen(noticiaExistente.getImagen());
            }
            noticiaExistente.setImagen(imagenUrl);
        }

        return noticiaService.guardar(noticiaExistente);
    }

    // ================== LISTAR TODAS (REQUIERE AUTENTICACIÓN) ==================
    @GetMapping
    public List<Noticia> listarTodas() {
        return noticiaService.obtenerTodas();
    }

    // ================== ELIMINAR (REQUIERE AUTENTICACIÓN) ==================
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        Noticia noticia = noticiaService.obtenerPorId(id);
        // Eliminar archivo si existe y es una ruta local
        if (noticia != null && noticia.getImagen() != null && !noticia.getImagen().startsWith("http")) {
            fileStorageService.eliminarImagen(noticia.getImagen());
        }
        noticiaService.eliminar(id);
    }
}
