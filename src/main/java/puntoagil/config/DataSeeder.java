package puntoagil.config;

import puntoagil.entity.Usuario;
import puntoagil.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@puntoagil.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRol(Usuario.Rol.ADMIN);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario Admin inicial creado: admin@puntoagil.com / Admin123!");
        }
    }
}