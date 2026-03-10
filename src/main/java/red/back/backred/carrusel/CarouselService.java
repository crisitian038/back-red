package red.back.backred.carrusel;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CarouselService {


    @Autowired
    private CarouselItemRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<CarouselItem> getAllItems() {
        return repository.findAllByOrderByDisplayOrderAsc();
    }

    public Optional<CarouselItem> getItemById(Long id) {
        return repository.findById(id);
    }

    public CarouselItem saveItem(CarouselItem item) {
        return repository.save(item);
    }

    public void deleteItem(Long id) {
        repository.deleteById(id);
        // Opcional: eliminar la imagen física
    }

    public String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        return "/uploads/" + filename;
    }

    public CarouselItem updateItem(Long id, CarouselItem itemDetails, MultipartFile imageFile) throws IOException {
        CarouselItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        item.setTitle(itemDetails.getTitle());
        item.setDescription(itemDetails.getDescription());
        item.setBackgroundColor(itemDetails.getBackgroundColor());
        item.setButtonText(itemDetails.getButtonText());
        item.setButtonUrl(itemDetails.getButtonUrl());
        item.setButtonEnabled(itemDetails.isButtonEnabled());
        item.setDisplayOrder(itemDetails.getDisplayOrder());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            item.setImageUrl(imageUrl);
        }

        return repository.save(item);
    }
}
