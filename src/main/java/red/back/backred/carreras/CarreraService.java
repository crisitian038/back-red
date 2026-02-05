package red.back.backred.carreras;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarreraService {

    private final CarreraRepository repo;

    /* ======================
       ADMIN
       ====================== */

    public Carrera crear(Carrera carrera) {
        carrera.setId(null);
        carrera.setActiva(true);
        carrera.setSolicitudes(0);
        System.out.println("🆕 Creando carrera: " + carrera.getNombre() +
                " | activa: " + carrera.isActiva());
        return repo.save(carrera);
    }

    public List<Carrera> listarTodas() {
        return repo.findAll();
    }

    public Carrera actualizar(Long id, Carrera data) {
        Carrera c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));

        System.out.println("🔄 Actualizando carrera ID: " + id);
        System.out.println("📊 Datos recibidos: activa=" + data.isActiva() +
                ", nombre=" + data.getNombre());

        c.setNombre(data.getNombre());
        c.setDescripcion(data.getDescripcion());
        c.setModalidad(data.getModalidad());
        c.setActiva(data.isActiva()); // ← ¡ESTA LÍNEA ES LA CLAVE!

        Carrera guardada = repo.save(c);
        System.out.println("✅ Carrera actualizada: activa=" + guardada.isActiva());
        return guardada;
    }

    public void cambiarEstado(Long id, boolean activa) {
        Carrera c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));

        c.setActiva(activa);
        repo.save(c);
        System.out.println("✅ Estado cambiado ID " + id + " a: " + activa);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /* ======================
       PÚBLICO
       ====================== */

    public List<Carrera> listarActivas() {
        return repo.findByActivaTrue();
    }

    public List<Carrera> top3() {
        return repo.findTop3ByActivaTrueOrderBySolicitudesDesc();
    }

    public void incrementarSolicitudes(String nombreCarrera) {
        repo.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombreCarrera))
                .findFirst()
                .ifPresent(c -> {
                    c.setSolicitudes(c.getSolicitudes() + 1);
                    repo.save(c);
                });
    }
}