package red.back.backred.ingles;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InglesRepository extends JpaRepository<InglesPrograma, Long> {
    List<InglesPrograma> findByEstado(EstadoInscripcionIngl estado);
}