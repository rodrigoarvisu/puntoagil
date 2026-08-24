package puntoagil.controller;

import puntoagil.entity.CorteCaja;
import puntoagil.entity.Usuario;
import puntoagil.service.CorteCajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cortes-caja")
public class CorteCajaController {

    @Autowired
    private CorteCajaService corteCajaService;

    @GetMapping
    public List<CorteCaja> listarTodos() {
        return corteCajaService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorteCaja> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(corteCajaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CorteCaja> generar(@RequestBody GenerarCorteRequest request) {
        Usuario usuario = new Usuario();
        usuario.setId(request.usuarioId());

        CorteCaja corte = corteCajaService.generarCorte(
                usuario, request.inicio(), request.fin(), request.efectivoContado());

        return ResponseEntity.ok(corte);
    }

    public record GenerarCorteRequest(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fin,
            BigDecimal efectivoContado
    ) {}
}