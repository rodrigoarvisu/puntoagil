package puntoagil.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import puntoagil.dto.LoginResponse;
import puntoagil.entity.Usuario;
import puntoagil.exception.SesionNoRenovableException;
import puntoagil.repository.UsuarioRepository;
import puntoagil.security.JwtUtil;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email.trim(), password));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        return new LoginResponse(
                jwtUtil.generarToken(usuario.getEmail()),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getRol().name());
    }

    /** Emite un token nuevo conservando el inicio de la sesión; respeta el tope de duración. */
    public String renovarToken(String token, String email) {
        Claims claims = jwtUtil.validar(token)
                .orElseThrow(() -> new SesionNoRenovableException("Sesión no válida o expirada"));

        long inicio = jwtUtil.inicioSesion(claims);
        if (jwtUtil.sesionAgotada(inicio)) {
            throw new SesionNoRenovableException(
                    "La sesión alcanzó su duración máxima. Inicia sesión de nuevo");
        }
        return jwtUtil.generarToken(email, inicio);
    }
}