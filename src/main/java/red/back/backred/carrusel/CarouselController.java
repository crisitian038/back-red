package red.back.backred.carrusel;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/carousel")
@CrossOrigin(origins = "http://localhost:5173")
public class CarouselController {

    @Autowired
    private CarouselService service;

    @GetMapping
    public List<CarouselItem> getAll() {
        return service.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarouselItem> getById(@PathVariable Long id) {
        return service.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<CarouselItem> create(
            @RequestPart("item") CarouselItem item,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            item.setImageUrl(service.saveImage(imageFile));
        }
        CarouselItem saved = service.saveItem(item);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<CarouselItem> update(
            @PathVariable Long id,
            @RequestPart("item") CarouselItem itemDetails,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        CarouselItem updated = service.updateItem(id, itemDetails, imageFile);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
