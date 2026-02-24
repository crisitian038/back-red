package red.back.backred.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/noticias}")
    private String uploadDir;

    /**
     * Guardar archivo de imagen y retornar la ruta relativa
     */
    public String guardarImagen(MultipartFile file) throws IOException {
        // Validar que sea un archivo
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }

        // Validar extensión
        String originalFilename = file.getOriginalFilename();
        String extension = obtenerExtension(originalFilename);
        if (!esImagenValida(extension)) {
            throw new IOException("Tipo de archivo no permitido. Solo se aceptan: jpg, jpeg, png, gif, webp");
        }

        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Generar nombre único
        String nombreArchivo = UUID.randomUUID() + "." + extension;
        Path rutaCompleta = uploadPath.resolve(nombreArchivo);

        // Guardar archivo
        Files.write(rutaCompleta, file.getBytes());

        // Retornar ruta relativa que el frontend pueda usar
        return "/uploads/noticias/" + nombreArchivo;
    }

    /**
     * Eliminar archivo de imagen
     */
    public void eliminarImagen(String rutaImagen) {
        if (rutaImagen == null || rutaImagen.isEmpty() || rutaImagen.startsWith("http")) {
            // No eliminar URLs externas
            return;
        }

        try {
            // Extraer nombre archivo de la ruta
            String nombreArchivo = rutaImagen.substring(rutaImagen.lastIndexOf("/") + 1);
            Path rutaCompleta = Paths.get(uploadDir, nombreArchivo);

            if (Files.exists(rutaCompleta)) {
                Files.delete(rutaCompleta);
            }
        } catch (IOException e) {
            // Log pero no fallar
            System.err.println("Error al eliminar archivo: " + e.getMessage());
        }
    }

    /**
     * Validar si es una extensión de imagen permitida
     */
    private boolean esImagenValida(String extension) {
        return extension.equalsIgnoreCase("jpg") ||
               extension.equalsIgnoreCase("jpeg") ||
               extension.equalsIgnoreCase("png") ||
               extension.equalsIgnoreCase("gif") ||
               extension.equalsIgnoreCase("webp");
    }

    /**
     * Obtener extensión del archivo
     */
    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
