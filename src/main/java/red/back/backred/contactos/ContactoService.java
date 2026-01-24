package red.back.backred.contactos;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactoService {

    private final ContactoRepository repository;

    public Contacto crearContacto(Contacto contacto) {
        contacto.setEstado(EstadoContacto.PENDIENTE);
        contacto.setFechaRegistro(LocalDateTime.now());
        return repository.save(contacto);
    }

    public Page<Contacto> listarTodos(int page, int size) {
        return repository.findAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by("fechaRegistro").descending()
                )
        );
    }

    public Page<Contacto> listarPorEstado(
            EstadoContacto estado,
            int page,
            int size) {

        return repository.findByEstado(
                estado,
                PageRequest.of(
                        page,
                        size,
                        Sort.by("fechaRegistro").descending()
                )
        );
    }

    public Contacto cambiarEstado(Long id, EstadoContacto nuevoEstado) {
        Contacto contacto = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Contacto no encontrado"));

        contacto.setEstado(nuevoEstado);
        return repository.save(contacto);
    }
}
