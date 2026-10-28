package puntoagil.service;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puntoagil.dto.UsuarioCreateRequest;
import puntoagil.dto.UsuarioUpdateRequest;
import puntoagil.entity.Usuario;
import puntoagil.exception.ConflictoException;
import puntoagil.exception.RecursoNoEncontradoException;
import puntoagil.exception.ReglaNegocioException;
import puntoagil.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(UsuarioCreateRequest r) {
        String email = normalizar(r.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictoException("Ya existe un usuario con ese email");
        }
        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(r.password()));
        u.setRol(r.rol());
        u.setActivo(true);
        Usuario guardado = usuarioRepository.save(u);
        log.info("Usuario creado id={} rol={}", guardado.getId(), guardado.getRol());
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    public Usuario actualizar(Long id, UsuarioUpdateRequest r) {
        Usuario u = buscarPorId(id);
        String email = normalizar(r.email());
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictoException("Ya existe un usuario con ese email");
        }
        if (u.getRol() == Usuario.Rol.ADMIN && r.rol() != Usuario.Rol.ADMIN) {
            exigirOtroAdminActivo(u);
        }
        u.setNombre(r.nombre().trim());
        u.setEmail(email);
        u.setRol(r.rol());
        log.info("Usuario actualizado id={} rol={}", id, r.rol());
        return u;
    }

    public Usuario cambiarEstado(Long id, boolean activo, String emailQuienActua) {
        Usuario u = buscarPorId(id);
        if (!activo) {
            if (u.getEmail().equalsIgnoreCase(emailQuienActua)) {
                throw new ReglaNegocioException("No puedes desactivar tu propia cuenta");
            }
            exigirOtroAdminActivo(u);
        }
        u.setActivo(activo);
        log.info("Usuario id={} activo={} (por {})", id, activo, emailQuienActua);
        return u;
    }

    public void cambiarPasswordPropia(String email, String actual, String nueva) {
        Usuario u = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        if (!passwordEncoder.matches(actual, u.getPassword())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta");
        }
        if (passwordEncoder.matches(nueva, u.getPassword())) {
            throw new ReglaNegocioException("La nueva contraseña debe ser distinta de la actual");
        }
        u.setPassword(passwordEncoder.encode(nueva));
        log.info("Usuario id={} cambió su contraseña", u.getId());
    }

    public void restablecerPassword(Long id, String nueva, String emailQuienActua) {
        Usuario u = buscarPorId(id);
        u.setPassword(passwordEncoder.encode(nueva));
        log.info("Contraseña de usuario id={} restablecida por {}", id, emailQuienActua);
    }

    // Si el usuario es un ADMIN activo, debe existir otro ADMIN activo
    private void exigirOtroAdminActivo(Usuario u) {
        if (u.getRol() == Usuario.Rol.ADMIN && u.isActivo()
                && usuarioRepository.countByRolAndActivoTrue(Usuario.Rol.ADMIN) <= 1) {
            throw new ReglaNegocioException("Debe quedar al menos un administrador activo");
        }
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}