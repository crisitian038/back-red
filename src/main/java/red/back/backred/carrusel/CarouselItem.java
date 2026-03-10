package red.back.backred.carrusel;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carousel_items")
@Data
@NoArgsConstructor
public class CarouselItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "background_color")
    private String backgroundColor;  // ej: "#f0f0f0"

    @Column(name = "button_text")
    @Size(max = 50)
    private String buttonText;

    @Column(name = "button_url")
    @Size(max = 255)
    private String buttonUrl;

    @Column(name = "button_enabled")
    private boolean buttonEnabled = false;

    @Column(name = "display_order")
    private Integer displayOrder;
}