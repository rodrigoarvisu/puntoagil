package puntoagil.controller;

import org.springframework.security.core.Authentication;
import puntoagil.dto.LoginResponse;
import puntoagil.entity.Usuario;
import puntoagil.repository.UsuarioRepository;
import puntoagil.security.JwtUtil;
import puntoagil.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.registrar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generarToken(usuario.getEmail());

        LoginResponse response = new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getRol().name()
        );

        return ResponseEntity.ok(response);
    }

    public record LoginRequest(String email, String password) {}
    public record AuthResponse(String token) {}
}