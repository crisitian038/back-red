package red.back.backred.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import red.back.backred.usuarios.Usuario;
import red.back.backred.usuarios.UsuarioRepository;
import red.back.backred.usuarios.Rol;
import red.back.backred.carreras.Carrera;
import red.back.backred.carreras.CarreraRepository;
import red.back.backred.contactos.Contacto;
import red.back.backred.contactos.ContactoRepository;
import red.back.backred.contactos.EstadoContacto;
import red.back.backred.inscripciones.Inscripcion;
import red.back.backred.inscripciones.InscripcionRepository;
import red.back.backred.inscripciones.EstadoInscripcion;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CarreraRepository carreraRepository;
    private final ContactoRepository contactoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔄 Iniciando carga de datos de prueba...");

        // Verificar si ya existen datos
        if (usuarioRepository.count() > 0) {
            System.out.println("✅ Los datos ya existen, omitiendo inicialización");
            return;
        }

        initializeUsuarios();
        initializeCarreras();
        initializeContactos();
        initializeInscripciones();

        System.out.println("✨ Carga de datos completada exitosamente");
    }

    private void initializeUsuarios() {
        System.out.println("📝 Insertando usuarios...");

        Usuario admin = Usuario.builder()
                .username("admin@admin.com")
                .email("admin@admin.com")
                .password(passwordEncoder.encode("123456"))
                .rol(Rol.ADMIN)
                .activo(true)
                .build();

        Usuario usuario = Usuario.builder()
                .username("usuario@prueba.com")
                .email("usuario@prueba.com")
                .password(passwordEncoder.encode("123456"))
                .rol(Rol.USER)
                .activo(true)
                .build();

        usuarioRepository.save(admin);
        usuarioRepository.save(usuario);

        System.out.println("✅ 2 usuarios insertados");
    }

    private void initializeCarreras() {
        System.out.println("📝 Insertando carreras...");

        Carrera carrera1 = Carrera.builder()
                .nombre("Ingeniería en Sistemas Computacionales")
                .descripcion("Carrera enfocada en desarrollo de software y gestión de tecnología. Duración: 4 años.")
                .modalidad("Presencial")
                .activa(true)
                .solicitudes(45)
                .build();

        Carrera carrera2 = Carrera.builder()
                .nombre("Contabilidad")
                .descripcion("Programa de contabilidad financiera y auditoría. Duración: 3.5 años.")
                .modalidad("En línea")
                .activa(true)
                .solicitudes(28)
                .build();

        carreraRepository.save(carrera1);
        carreraRepository.save(carrera2);

        System.out.println("✅ 2 carreras insertadas");
    }

    private void initializeContactos() {
        System.out.println("📝 Insertando contactos...");

        Contacto contacto1 = Contacto.builder()
                .nombreCompleto("Juan Pérez García")
                .email("juan.perez@email.com")
                .telefono("5551234567")
                .asunto("Información sobre inscripciones")
                .mensaje("Quisiera conocer más información sobre el proceso de inscripción y los requisitos necesarios.")
                .estado(EstadoContacto.PENDIENTE)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Contacto contacto2 = Contacto.builder()
                .nombreCompleto("María López Martínez")
                .email("maria.lopez@email.com")
                .telefono("5559876543")
                .asunto("Consulta sobre modalidad en línea")
                .mensaje("Me gustaría saber si existe la opción de estudiar completamente en línea.")
                .estado(EstadoContacto.REVISADA)
                .fechaRegistro(LocalDateTime.now())
                .build();

        contactoRepository.save(contacto1);
        contactoRepository.save(contacto2);

        System.out.println("✅ 2 contactos insertados");
    }

    private void initializeInscripciones() {
        System.out.println("📝 Insertando inscripciones...");

        Inscripcion inscripcion1 = Inscripcion.builder()
                .nombreCompleto("Carlos Rodríguez Sánchez")
                .email("carlos.rodriguez@email.com")
                .telefono("5551111111")
                .curp("ROSC000101HDFXXX00")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .carrera("Ingeniería en Sistemas Computacionales")
                .estado(EstadoInscripcion.EN_PROCESO)
                .build();

        Inscripcion inscripcion2 = Inscripcion.builder()
                .nombreCompleto("Ana Martínez Torres")
                .email("ana.martinez@email.com")
                .telefono("5552222222")
                .curp("MATA000202HDFYYY00")
                .fechaNacimiento(LocalDate.of(2000, 2, 2))
                .carrera("Contabilidad")
                .estado(EstadoInscripcion.INSCRITO)
                .build();

        inscripcionRepository.save(inscripcion1);
        inscripcionRepository.save(inscripcion2);

        System.out.println("✅ 2 inscripciones insertadas");
    }
}
