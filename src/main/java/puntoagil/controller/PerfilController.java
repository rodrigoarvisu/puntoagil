package puntoagil.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import puntoagil.dto.CambiarPasswordRequest;
import puntoagil.service.UsuarioService;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {
    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> cambiarPassword(Authentication auth,
                                                @Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPasswordPropia(auth.getName(), request.passwordActual(), request.passwordNueva());
        return ResponseEntity.noContent().build();
    }
}
