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
import red.back.backred.ingles.EstadoInscripcionIngl;
import red.back.backred.ingles.InglesPrograma;
import red.back.backred.ingles.InglesRepository;
import red.back.backred.bachillerato286.Bachillerato286Inscripcion;
import red.back.backred.bachillerato286.Bachillerato286Repository;
import red.back.backred.carrerasEjecutivas.CarreraEjecutiva;
import red.back.backred.carrerasEjecutivas.CarreraEjecutivaRepository;
import red.back.backred.carrerasEjecutivas.CarrerasEjecutivasInscripcion;
import red.back.backred.carrerasEjecutivas.CarrerasEjecutivasRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CarreraRepository carreraRepository;
    private final ContactoRepository contactoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final InglesRepository inglesRepository;
    private final Bachillerato286Repository bachillerato286Repository;
    private final CarreraEjecutivaRepository carreraEjecutivaRepository;
    private final CarrerasEjecutivasRepository carrerasEjecutivasRepository;
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
        initializeCarrerasEjecutivas();
        initializeContactos();
        initializeInscripciones();
        initializeInglesInscripciones();
        initializeBachillerato286Inscripciones();
        initializeCarrerasEjecutivasInscripciones();

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

    private void initializeCarrerasEjecutivas() {
        System.out.println("📝 Insertando carreras ejecutivas...");

        CarreraEjecutiva ejecutiva1 = CarreraEjecutiva.builder()
                .nombre("MBA - Administración de Empresas")
                .descripcion("Programa de Maestría en Administración de Empresas enfocado en liderazgo, finanzas y gestión estratégica. Para profesionales con experiencia laboral.")
                .modalidad("EN LÍNEA")
                .activa(true)
                .solicitudes(52)
                .build();

        CarreraEjecutiva ejecutiva2 = CarreraEjecutiva.builder()
                .nombre("Especialización en Marketing Digital")
                .descripcion("Programa especializado en estrategias digitales, SEO, redes sociales y analítica web. Ideal para profesionales del marketing.")
                .modalidad("EN LÍNEA")
                .activa(true)
                .solicitudes(38)
                .build();

        CarreraEjecutiva ejecutiva3 = CarreraEjecutiva.builder()
                .nombre("Diplomado en Finanzas Corporativas")
                .descripcion("Programa de diplomado en análisis financiero, presupuesto corporativo y gestión de riesgos. Para profesionales del área financiera.")
                .modalidad("HÍBRIDA")
                .activa(true)
                .solicitudes(31)
                .build();

        carreraEjecutivaRepository.save(ejecutiva1);
        carreraEjecutivaRepository.save(ejecutiva2);
        carreraEjecutivaRepository.save(ejecutiva3);

        System.out.println("✅ 3 carreras ejecutivas insertadas");
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
        System.out.println("📝 Insertando inscripciones Bachillerato...");

        Inscripcion inscripcion1 = Inscripcion.builder()
                .nombreCompleto("Carlos Rodríguez Sánchez")
                .email("carlos.rodriguez@email.com")
                .telefono("5551111111")
                .curp("ROSC000101HDFXXX00")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .estado(EstadoInscripcion.EN_PROCESO)
                .programaTipo("bachillerato")
                .build();

        Inscripcion inscripcion2 = Inscripcion.builder()
                .nombreCompleto("Ana Martínez Torres")
                .email("ana.martinez@email.com")
                .telefono("5552222222")
                .curp("MATA000202HDFYYY00")
                .fechaNacimiento(LocalDate.of(2000, 2, 2))
                .estado(EstadoInscripcion.INSCRITO)
                .programaTipo("bachillerato")
                .build();

        inscripcionRepository.save(inscripcion1);
        inscripcionRepository.save(inscripcion2);

        System.out.println("✅ 2 inscripciones Bachillerato insertadas");
    }

    private void initializeInglesInscripciones() {
        System.out.println("📝 Insertando inscripciones Inglés...");

        InglesPrograma ingles1 = new InglesPrograma();
        ingles1.setNombreCompleto("David González López");
        ingles1.setEmail("david.gonzalez@email.com");
        ingles1.setTelefono("5553333333");
        ingles1.setCurp("GOLD000303HDFZZZ00");
        ingles1.setFechaNacimiento("1998-03-15");
        ingles1.setEstado(EstadoInscripcionIngl.CANCELADO);
        ingles1.setFechaRegistro(LocalDateTime.now());

        InglesPrograma ingles2 = new InglesPrograma();
        ingles2.setNombreCompleto("Patricia Sánchez Ruiz");
        ingles2.setEmail("patricia.sanchez@email.com");
        ingles2.setTelefono("5554444444");
        ingles2.setCurp("SARP000404HDFAAA00");
        ingles2.setFechaNacimiento("1997-04-20");
        ingles2.setEstado(EstadoInscripcionIngl.CONFIRMADO);
        ingles2.setFechaRegistro(LocalDateTime.now());

        inglesRepository.save(ingles1);
        inglesRepository.save(ingles2);

        System.out.println("✅ 2 inscripciones Inglés insertadas");
    }

    private void initializeBachillerato286Inscripciones() {
        System.out.println("📝 Insertando inscripciones Bachillerato 286...");

        Bachillerato286Inscripcion bach286_1 = new Bachillerato286Inscripcion();
        bach286_1.setNombreCompleto("Roberto Jiménez Flores");
        bach286_1.setEmail("roberto.jimenez@email.com");
        bach286_1.setTelefono("5555555555");
        bach286_1.setCurp("JIFR000505HDFBBB00");
        bach286_1.setFechaNacimiento("1996-05-10");
        bach286_1.setEstado(EstadoInscripcion.EN_PROCESO);
        bach286_1.setFechaRegistro(LocalDateTime.now());

        Bachillerato286Inscripcion bach286_2 = new Bachillerato286Inscripcion();
        bach286_2.setNombreCompleto("Isabel Castro Velázquez");
        bach286_2.setEmail("isabel.castro@email.com");
        bach286_2.setTelefono("5556666666");
        bach286_2.setCurp("CAVI000606HDFCCC00");
        bach286_2.setFechaNacimiento("1995-06-25");
        bach286_2.setEstado(EstadoInscripcion.RECHAZADA);
        bach286_2.setFechaRegistro(LocalDateTime.now());

        bachillerato286Repository.save(bach286_1);
        bachillerato286Repository.save(bach286_2);

        System.out.println("✅ 2 inscripciones Bachillerato 286 insertadas");
    }

    private void initializeCarrerasEjecutivasInscripciones() {
        System.out.println("📝 Insertando inscripciones Carreras Ejecutivas...");

        CarrerasEjecutivasInscripcion ejecutiva1 = new CarrerasEjecutivasInscripcion();
        ejecutiva1.setNombreCompleto("Felipe Hernández Gutiérrez");
        ejecutiva1.setEmail("felipe.hernandez@email.com");
        ejecutiva1.setTelefono("5557777777");
        ejecutiva1.setCurp("HEGF000707HDFDDDD00");
        ejecutiva1.setFechaNacimiento("1990-07-30");
        ejecutiva1.setEstado(EstadoInscripcion.EN_PROCESO);
        ejecutiva1.setFechaRegistro(LocalDateTime.now());

        CarrerasEjecutivasInscripcion ejecutiva2 = new CarrerasEjecutivasInscripcion();
        ejecutiva2.setNombreCompleto("Gabriela Moreno Díaz");
        ejecutiva2.setEmail("gabriela.moreno@email.com");
        ejecutiva2.setTelefono("5558888888");
        ejecutiva2.setCurp("MODG000808HDFEEEE00");
        ejecutiva2.setFechaNacimiento("1988-08-14");
        ejecutiva2.setEstado(EstadoInscripcion.INSCRITO);
        ejecutiva2.setFechaRegistro(LocalDateTime.now());

        carrerasEjecutivasRepository.save(ejecutiva1);
        carrerasEjecutivasRepository.save(ejecutiva2);

        System.out.println("✅ 2 inscripciones Carreras Ejecutivas insertadas");
    }
}
