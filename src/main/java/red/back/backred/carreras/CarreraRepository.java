package red.back.backred.carreras;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findByActivaTrue();

    List<Carrera> findTop3ByActivaTrueOrderBySolicitudesDesc();

    boolean existsByNombreIgnoreCase(String nombre);
}
