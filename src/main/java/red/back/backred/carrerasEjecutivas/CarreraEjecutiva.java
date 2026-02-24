package red.back.backred.carrerasEjecutivas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carreras_ejecutivas")
public class CarreraEjecutiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    private String modalidad;

    @Column(nullable = false)
    private boolean activa;

    private int solicitudes;
}
