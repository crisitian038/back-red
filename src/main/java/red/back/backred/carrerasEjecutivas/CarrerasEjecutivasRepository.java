package red.back.backred.carrerasEjecutivas;

import org.springframework.data.jpa.repository.JpaRepository;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.util.List;

public interface CarrerasEjecutivasRepository extends JpaRepository<CarrerasEjecutivasInscripcion, Long> {
    List<CarrerasEjecutivasInscripcion> findByEstado(EstadoInscripcion estado);
}
