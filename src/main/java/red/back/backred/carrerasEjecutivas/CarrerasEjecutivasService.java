package red.back.backred.carrerasEjecutivas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrerasEjecutivasService {

    private final CarrerasEjecutivasRepository repository;

    public CarrerasEjecutivasInscripcion crear(CarrerasEjecutivasInscripcion inscripcion) {
        inscripcion.setEstado(EstadoInscripcion.EN_PROCESO);
        inscripcion.setFechaRegistro(LocalDateTime.now());
        return repository.save(inscripcion);
    }

    public List<CarrerasEjecutivasInscripcion> listarTodas() {
        return repository.findAll();
    }

    public CarrerasEjecutivasInscripcion obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<CarrerasEjecutivasInscripcion> listarPorEstado(EstadoInscripcion estado) {
        return repository.findByEstado(estado);
    }

    public void cambiarEstado(Long id, EstadoInscripcion estado) {
        CarrerasEjecutivasInscripcion inscripcion = obtenerPorId(id);
        inscripcion.setEstado(estado);
        repository.save(inscripcion);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
