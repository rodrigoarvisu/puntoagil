package puntoagil.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import puntoagil.dto.MetodoPagoRequest;
import puntoagil.dto.MetodoPagoResponse;
import puntoagil.service.MetodoPagoService;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoService service;

    public MetodoPagoController(MetodoPagoService service) {
        this.service = service;
    }

    @GetMapping("/activos")
    public List<MetodoPagoResponse> activos() {
        return service.listarActivos().stream().map(MetodoPagoResponse::desde).toList();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<MetodoPagoResponse> todos() {
        return service.listarTodos().stream().map(MetodoPagoResponse::desde).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MetodoPagoResponse> crear(@Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MetodoPagoResponse.desde(service.crear(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MetodoPagoResponse actualizar(@PathVariable Long id, @Valid @RequestBody MetodoPagoRequest request) {
        return MetodoPagoResponse.desde(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public MetodoPagoResponse cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return MetodoPagoResponse.desde(service.cambiarEstado(id, activo));
    }
}
