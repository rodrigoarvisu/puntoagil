package puntoagil.controller;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import puntoagil.dto.LoginResponse;
import puntoagil.entity.Usuario;
import puntoagil.repository.UsuarioRepository;
import puntoagil.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UsuarioRepository usuarioRepository;
    private JwtUtil jwtUtil;

    public AuthController( AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this. usuarioRepository = usuarioRepository;
        this. jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
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

    @PostMapping("/renovar")
    public ResponseEntity<Map<String, String>> renovar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            Authentication auth) {

        Claims claims = jwtUtil.validar(authHeader.substring(7)).orElse(null);
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Sesión no válida o expirada"));
        }

        long inicio = jwtUtil.inicioSesion(claims);
        if (jwtUtil.sesionAgotada(inicio)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "La sesión alcanzó su duración máxima. Inicia sesión de nuevo"));
        }

        return ResponseEntity.ok(Map.of("token", jwtUtil.generarToken(auth.getName(), inicio)));
    }

    public record LoginRequest(String email, String password) {}
}