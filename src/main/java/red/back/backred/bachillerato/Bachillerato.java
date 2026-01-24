package red.back.backred.bachillerato;

import jakarta.persistence.*;
import lombok.*;
import red.back.backred.usuarios.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "bachillerato",
        indexes = {
                @Index(name = "idx_bach_estado", columnList = "estado"),
                @Index(name = "idx_bach_usuario", columnList = "usuario_id")
        }
)
public class Bachillerato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String email;
    private String telefono;
    private String curp;

    private LocalDate fechaNacimiento;

    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    private EstadoBachillerato estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
