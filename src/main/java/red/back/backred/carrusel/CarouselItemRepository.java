package red.back.backred.carrusel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarouselItemRepository extends JpaRepository<CarouselItem, Long> {
    List<CarouselItem> findAllByOrderByDisplayOrderAsc();
}