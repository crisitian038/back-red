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
        System.out.println("🆕 CREANDO INSCRIPCIÓN BACHILLERATO (PÚBLICO):");
        System.out.println("   Nombre: " + bachillerato.getNombreCompleto());
        System.out.println("   Email: " + bachillerato.getEmail());
        System.out.println("   Teléfono: " + bachillerato.getTelefono());
        System.out.println("   CURP: " + bachillerato.getCurp());
        System.out.println("   Fecha Nacimiento: " + bachillerato.getFechaNacimiento());


        // Establecer valores automáticos
        bachillerato.setFechaRegistro(LocalDateTime.now());
        bachillerato.setEstado(EstadoBachillerato.EN_PROCESO);

        // Guardar en BD
        Bachillerato guardado = repo.save(bachillerato);
        System.out.println("✅ INSCRIPCIÓN GUARDADA ID: " + guardado.getId());
        return guardado;
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

        System.out.println("🔄 CAMBIANDO ESTADO BACHILLERATO ID " + id + " a: " + estado);
        bachillerato.setEstado(estado);
        return repo.save(bachillerato);
    }

}