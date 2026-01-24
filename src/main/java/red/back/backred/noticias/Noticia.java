package red.back.backred.noticias;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "noticias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcionCorta;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String introduccion;


    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String contenido;


    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imagen;

    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean publicada;
}
