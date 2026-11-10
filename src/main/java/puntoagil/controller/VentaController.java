package puntoagil.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import puntoagil.dto.VentaRequest;
import puntoagil.dto.VentaResponse;
import puntoagil.entity.Venta;
import puntoagil.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaResponse> listarTodas(Authentication auth) {
        boolean admin = esAdmin(auth);
        return ventaService.listarTodas().stream().map(v -> VentaResponse.desde(v, admin)).toList();
    }

    @GetMapping("/{id}")
    public VentaResponse buscarPorId(@PathVariable Long id, Authentication auth) {
        return VentaResponse.desde(ventaService.buscarPorId(id), esAdmin(auth));
    }

    @GetMapping("/rango")
    public List<VentaResponse> buscarPorRango(@RequestParam LocalDateTime inicio,
                                              @RequestParam LocalDateTime fin,
                                              Authentication auth) {
        boolean admin = esAdmin(auth);
        return ventaService.buscarPorRangoFechas(inicio, fin).stream()
                .map(v -> VentaResponse.desde(v, admin)).toList();
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request,
                                                   Authentication auth) {
        Venta venta = ventaService.registrarVenta(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VentaResponse.desde(venta, esAdmin(auth)));
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}