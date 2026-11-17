package puntoagil.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import puntoagil.dto.LoginResponse;
import puntoagil.dto.TokenResponse;
import puntoagil.exception.SesionNoRenovableException;
import puntoagil.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @PostMapping("/renovar")
    public TokenResponse renovar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                 Authentication auth) {
        String token = authHeader.substring(7); // el filtro ya comprobó que empieza con "Bearer "
        return new TokenResponse(authService.renovarToken(token, auth.getName()));
    }

    // Cualquier fallo de autenticación (contraseña mala, usuario inexistente,
    // cuenta desactivada) responde exactamente lo mismo.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> credencialesInvalidas() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Email o contraseña incorrectos"));
    }

    @ExceptionHandler(SesionNoRenovableException.class)
    public ResponseEntity<Map<String, String>> sesionNoRenovable(SesionNoRenovableException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", e.getMessage()));
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}
}