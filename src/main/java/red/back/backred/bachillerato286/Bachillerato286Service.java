package red.back.backred.bachillerato286;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Bachillerato286Service {

    private final Bachillerato286Repository repository;

    public Bachillerato286Inscripcion crear(Bachillerato286Inscripcion inscripcion) {
        inscripcion.setEstado(EstadoInscripcion.EN_PROCESO);
        inscripcion.setFechaRegistro(LocalDateTime.now());
        return repository.save(inscripcion);
    }

    public List<Bachillerato286Inscripcion> listarTodas() {
        return repository.findAll();
    }

    public Bachillerato286Inscripcion obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Bachillerato286Inscripcion> listarPorEstado(EstadoInscripcion estado) {
        return repository.findByEstado(estado);
    }

    public void cambiarEstado(Long id, EstadoInscripcion estado) {
        Bachillerato286Inscripcion inscripcion = obtenerPorId(id);
        inscripcion.setEstado(estado);
        repository.save(inscripcion);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
