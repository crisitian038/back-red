package red.back.backred.contactos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    Page<Contacto> findByEstado(EstadoContacto estado, Pageable pageable);
}
