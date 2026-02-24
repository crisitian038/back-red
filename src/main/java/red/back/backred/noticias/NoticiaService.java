package red.back.backred.noticias;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import red.back.backred.config.FileStorageService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final FileStorageService fileStorageService;

    // ADMIN
    public List<Noticia> obtenerTodas() {
        return noticiaRepository.findAll();
    }

    public Noticia guardar(Noticia noticia) {
        System.out.println("🔵 SERVICE.GUARDAR() llamado");
        System.out.println("   Título: " + noticia.getTitulo());
        System.out.println("   Imagen: " + noticia.getImagen());
        System.out.println("   Publicada: " + noticia.getPublicada());
        
        if (noticia.getFecha() == null) {
            noticia.setFecha(LocalDate.now());
            System.out.println("   Asignando fecha: " + LocalDate.now());
        }
        if (noticia.getPublicada() == null) {
            noticia.setPublicada(false);
            System.out.println("   Publicada era null, asignando false");
        }
        
        System.out.println("💾 Llamando a repository.save()...");
        Noticia resultado = noticiaRepository.save(noticia);
        
        System.out.println("✅ GUARDADA EN BD");
        System.out.println("   ID: " + resultado.getId());
        System.out.println("   Título guardado: " + resultado.getTitulo());
        System.out.println("   Imagen guardada: " + resultado.getImagen());
        
        return resultado;
    }

    public Noticia obtenerPorId(Long id) {
        return noticiaRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        noticiaRepository.deleteById(id);
    }

    // PUBLICO
    public List<Noticia> obtenerPublicadas() {
        return noticiaRepository.findByPublicadaTrue();
    }
}
