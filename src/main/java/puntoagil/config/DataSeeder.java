package puntoagil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import puntoagil.entity.Usuario;
import puntoagil.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.admin.email:}") String adminEmail,
                      @Value("${app.admin.password:}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) return;

        if (adminEmail.isBlank() || adminPassword.length() < 12) {
            log.warn("BD sin usuarios y ADMIN_EMAIL / ADMIN_PASSWORD (mín. 12 caracteres) no configurados: no se creó el administrador inicial.");
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setEmail(adminEmail.trim().toLowerCase());
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRol(Usuario.Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);
        log.info("Administrador inicial creado: {}", admin.getEmail());
    }
}