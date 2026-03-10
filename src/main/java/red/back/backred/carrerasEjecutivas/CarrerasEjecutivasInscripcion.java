package red.back.backred.carrerasEjecutivas;

import jakarta.persistence.*;
import lombok.*;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carreras_ejecutivas_inscripciones")
public class CarrerasEjecutivasInscripcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String email;

    private String telefono;

    private String curp;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String carrera; // Nombre de la carrera ejecutiva

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoInscripcion.EN_PROCESO;
        }
    }
}
