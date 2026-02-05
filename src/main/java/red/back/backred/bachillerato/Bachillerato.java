package red.back.backred.bachillerato;

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
@Table(name = "bachillerato")
public class Bachillerato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String email;

    private String telefono;

    private String curp;

    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBachillerato estado;

    // ⚠️ ¡ELIMINAR ESTA RELACIÓN COMPLETAMENTE!
    // @ManyToOne
    // @JoinColumn(name = "usuario_id") // ← QUITAR
    // private Usuario usuario; // ← QUITAR
}