package red.back.backred.carrerasEjecutivas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarreraEjecutivaService {

    private final CarreraEjecutivaRepository repo;

    public CarreraEjecutiva crear(CarreraEjecutiva c) {
        c.setId(null);
        c.setActiva(true);
        c.setSolicitudes(0);
        return repo.save(c);
    }

    public List<CarreraEjecutiva> listarTodas() {
        return repo.findAll();
    }

    public CarreraEjecutiva actualizar(Long id, CarreraEjecutiva data) {
        CarreraEjecutiva c = repo.findById(id).orElseThrow(() -> new RuntimeException("No encontrada"));
        c.setNombre(data.getNombre());
        c.setDescripcion(data.getDescripcion());
        c.setModalidad(data.getModalidad());
        c.setActiva(data.isActiva());
        return repo.save(c);
    }

    public void cambiarEstado(Long id, boolean activa) {
        CarreraEjecutiva c = repo.findById(id).orElseThrow(() -> new RuntimeException("No encontrada"));
        c.setActiva(activa);
        repo.save(c);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<CarreraEjecutiva> listarActivas() {
        return repo.findByActivaTrue();
    }

    public List<CarreraEjecutiva> top3() {
        return repo.findTop3ByActivaTrueOrderBySolicitudesDesc();
    }
}
