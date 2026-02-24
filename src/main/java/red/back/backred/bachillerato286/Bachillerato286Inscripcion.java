package red.back.backred.bachillerato286;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.time.LocalDateTime;

@Entity
@Table(name = "bachillerato_286_inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bachillerato286Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String email;
    private String telefono;
    private String curp;
    private String fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado;

    private LocalDateTime fechaRegistro;
}
