package red.back.backred.carrerasEjecutivas;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarreraEjecutivaRepository extends JpaRepository<CarreraEjecutiva, Long> {
    List<CarreraEjecutiva> findByActivaTrue();
    List<CarreraEjecutiva> findTop3ByActivaTrueOrderBySolicitudesDesc();
}
