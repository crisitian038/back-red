package red.back.backred.inscripciones;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;

    private String email;

    private String telefono;

    private String curp;

    private LocalDate fechaNacimiento;

    private String carrera;

    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado;

    private LocalDateTime fechaRegistro;

    @PrePersist
    void onCreate() {
        fechaRegistro = LocalDateTime.now();
        estado = EstadoInscripcion.EN_PROCESO;
    }
}
