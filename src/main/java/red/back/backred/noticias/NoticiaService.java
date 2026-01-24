package red.back.backred.noticias;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;

    // ADMIN
    public List<Noticia> obtenerTodas() {
        return noticiaRepository.findAll();
    }

    public Noticia guardar(Noticia noticia) {
        if (noticia.getFecha() == null) {
            noticia.setFecha(LocalDate.now());
        }
        if (noticia.getPublicada() == null) {
            noticia.setPublicada(false);
        }
        return noticiaRepository.save(noticia);
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
