package red.back.backred.inscripciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    Page<Inscripcion> findByEstado(EstadoInscripcion estado, Pageable pageable);
}
