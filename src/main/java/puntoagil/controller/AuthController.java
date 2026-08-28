package puntoagil.controller;

import puntoagil.dto.LoginResponse;
import puntoagil.entity.Usuario;
import puntoagil.repository.UsuarioRepository;
import puntoagil.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

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
}