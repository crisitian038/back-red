package red.back.backred.bachillerato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BachilleratoRepository
        extends JpaRepository<Bachillerato, Long> {

    Page<Bachillerato> findByEstado(
            EstadoBachillerato estado,
            Pageable pageable
    );
}
