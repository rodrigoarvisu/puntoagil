package puntoagil.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import puntoagil.dto.ResetPasswordRequest;
import puntoagil.dto.UsuarioCreateRequest;
import puntoagil.dto.UsuarioResponse;
import puntoagil.dto.UsuarioUpdateRequest;
import puntoagil.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos().stream().map(UsuarioResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return UsuarioResponse.desde(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.desde(usuarioService.registrar(request)));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody UsuarioUpdateRequest request) {
        return UsuarioResponse.desde(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(@PathVariable Long id, @RequestParam boolean activo,
                                         Authentication auth) {
        return UsuarioResponse.desde(usuarioService.cambiarEstado(id, activo, auth.getName()));
    }

    // El admin restablece la clave de otro usuario (sin pedir la actual)
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> restablecerPassword(@PathVariable Long id,
                                                    @Valid @RequestBody ResetPasswordRequest request,
                                                    Authentication auth) {
        usuarioService.restablecerPassword(id, request.passwordNueva(), auth.getName());
        return ResponseEntity.noContent().build();
    }
}