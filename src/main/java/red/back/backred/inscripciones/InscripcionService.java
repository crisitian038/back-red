package red.back.backred.inscripciones;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import red.back.backred.carreras.CarreraService;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository repo;
    private final CarreraService carreraService;

    public Inscripcion crear(Inscripcion inscripcion) {

        // 1️⃣ VALIDAR EDAD MÍNIMA
        validarEdad(inscripcion.getFechaNacimiento());

        // 2️⃣ GUARDAR INSCRIPCIÓN
        Inscripcion guardada = repo.save(inscripcion);

        // 3️⃣ INCREMENTAR POPULARIDAD DE LA CARRERA
        carreraService.incrementarSolicitudes(inscripcion.getCarrera());

        // 4️⃣ RETORNAR INSCRIPCIÓN
        return guardada;
    }

    public Page<Inscripcion> listar(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<Inscripcion> listarPorEstado(EstadoInscripcion estado, Pageable pageable) {
        return repo.findByEstado(estado, pageable);
    }

    public Inscripcion cambiarEstado(Long id, EstadoInscripcion estado) {
        Inscripcion inscripcion = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        inscripcion.setEstado(estado);
        return repo.save(inscripcion);
    }

    // 🔒 REGLA DE NEGOCIO
    private void validarEdad(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 25) {
            throw new RuntimeException("Debe tener al menos 25 años para inscribirse");
        }
    }
}
