package puntoagil.service;

import puntoagil.dto.UsuarioUpdateRequest;
import puntoagil.entity.Usuario;
import puntoagil.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public Usuario actualizar(Long id, UsuarioUpdateRequest datos) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(datos.nombre());
        usuario.setEmail(datos.email());
        usuario.setRol(datos.rol());
        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        Usuario usuario = buscarPorId(id);
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }
}