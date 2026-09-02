package puntoagil.controller;

import puntoagil.entity.AperturaCaja;
import puntoagil.entity.Usuario;
import puntoagil.service.AperturaCajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/apertura-caja")
public class AperturaCajaController {

    @Autowired
    private AperturaCajaService aperturaCajaService;

    @GetMapping("/activa")
    public ResponseEntity<?> obtenerActiva() {
        return aperturaCajaService.obtenerAperturaActiva()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<AperturaCaja> abrir(@RequestBody AbrirCajaRequest request) {
        Usuario usuario = new Usuario();
        usuario.setId(request.usuarioId());
        return ResponseEntity.ok(aperturaCajaService.abrirCaja(usuario, request.montoInicial()));
    }

    public record AbrirCajaRequest(Long usuarioId, BigDecimal montoInicial) {}
}