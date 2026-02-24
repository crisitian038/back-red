package red.back.backred.bachillerato286;

import org.springframework.data.jpa.repository.JpaRepository;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.util.List;

public interface Bachillerato286Repository extends JpaRepository<Bachillerato286Inscripcion, Long> {
    List<Bachillerato286Inscripcion> findByEstado(EstadoInscripcion estado);
}
