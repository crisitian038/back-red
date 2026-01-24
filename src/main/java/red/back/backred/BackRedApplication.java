package red.back.backred;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import red.back.backred.usuarios.Rol;
import red.back.backred.usuarios.Usuario;
import red.back.backred.usuarios.UsuarioRepository;

@SpringBootApplication
@EnableMethodSecurity
public class BackRedApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackRedApplication.class, args);
    }

    @Bean
    CommandLineRunner initUsers(
            UsuarioRepository repo,
            PasswordEncoder encoder) {

        return args -> {

            // ========= ADMIN POR DEFECTO =========
            boolean adminExiste =
                    repo.findByEmail("admin@admin.com").isPresent();

            if (!adminExiste) {
                repo.save(
                        Usuario.builder()
                                .username("Administrador")
                                .email("admin@admin.com") // ✅ corregido
                                .password(encoder.encode("123456"))
                                .rol(Rol.ADMIN)
                                .activo(true)
                                .build()
                );
                System.out.println("✅ Admin creado");
            } else {
                System.out.println("✔ Admin ya existe");
            }

            // ========= USUARIO COMÚN POR DEFECTO =========
            boolean userExiste =
                    repo.findByEmail("user@user.com").isPresent();

            if (!userExiste) {
                repo.save(
                        Usuario.builder()
                                .username("Usuario Prueba")
                                .email("user@user.com")
                                .password(encoder.encode("123456"))
                                .rol(Rol.USER)
                                .activo(true)
                                .build()
                );
                System.out.println("✅ Usuario de prueba creado");
            } else {
                System.out.println("✔ Usuario de prueba ya existe");
            }
        };
    }
}
