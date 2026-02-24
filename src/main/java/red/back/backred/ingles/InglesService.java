package red.back.backred.ingles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import red.back.backred.ingles.EstadoInscripcionIngl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InglesService {

    private final InglesRepository repo;

    public InglesPrograma crear(InglesPrograma inscripcion) {
        return repo.save(inscripcion);
    }

    public List<InglesPrograma> listarTodas() {
        return repo.findAll();
    }

    public InglesPrograma obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<InglesPrograma> listarPorEstado(EstadoInscripcionIngl estado) {
        return repo.findByEstado(estado);
    }

    public void cambiarEstado(Long id, EstadoInscripcionIngl estado) {
        InglesPrograma i = repo.findById(id).orElse(null);
        if (i != null) {
            i.setEstado(estado);
            repo.save(i);
        }
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}

