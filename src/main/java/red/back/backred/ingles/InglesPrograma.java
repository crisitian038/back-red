package red.back.backred.ingles;

import jakarta.persistence.*;
import lombok.*;
// import red.back.backred.inscripciones.EstadoInscripcion; // ← Elimina esta línea
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ingles_inscripciones")
public class InglesPrograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String email;

    private String telefono;

    private String curp;

    private String fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcionIngl estado; // Cambiado al nuevo enum

    private LocalDateTime fechaRegistro;

    @PrePersist
    void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoInscripcionIngl.PENDIENTE; // Usa el valor del nuevo enum
        }
    }
}