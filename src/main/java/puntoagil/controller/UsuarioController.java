package puntoagil.controller;

import puntoagil.dto.CambiarPasswordRequest;
import puntoagil.dto.UsuarioCreateRequest;
import puntoagil.dto.UsuarioResponse;
import puntoagil.dto.UsuarioUpdateRequest;
import puntoagil.entity.Usuario;
import puntoagil.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos().stream()
                .map(UsuarioResponse::desde)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.desde(usuarioService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioCreateRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());
        usuario.setRol(request.rol());
        Usuario creado = usuarioService.registrar(usuario);
        return ResponseEntity.ok(UsuarioResponse.desde(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
        Usuario actualizado = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(UsuarioResponse.desde(actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        Usuario actualizado = usuarioService.cambiarEstado(id, activo);
        return ResponseEntity.ok(UsuarioResponse.desde(actualizado));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id, @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(id, request.passwordActual(), request.passwordNueva());
        return ResponseEntity.noContent().build();
    }
}