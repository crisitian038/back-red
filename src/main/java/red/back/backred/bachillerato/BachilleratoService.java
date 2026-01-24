package red.back.backred.bachillerato;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BachilleratoService {

    private final BachilleratoRepository repo;

    public Bachillerato crear(Bachillerato bachillerato) {
        bachillerato.setFechaRegistro(LocalDateTime.now());
        bachillerato.setEstado(EstadoBachillerato.EN_PROCESO);
        return repo.save(bachillerato);
    }

    public Page<Bachillerato> listar(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<Bachillerato> listarPorEstado(
            EstadoBachillerato estado,
            Pageable pageable
    ) {
        return repo.findByEstado(estado, pageable);
    }

    public Page<Bachillerato> listarOpcional(
            EstadoBachillerato estado,
            Pageable pageable
    ) {
        if (estado == null) {
            return repo.findAll(pageable);
        }
        return repo.findByEstado(estado, pageable);
    }

    public Bachillerato cambiarEstado(Long id, EstadoBachillerato estado) {
        Bachillerato bachillerato = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Registro no encontrado"));

        bachillerato.setEstado(estado);
        return repo.save(bachillerato);
    }
}
