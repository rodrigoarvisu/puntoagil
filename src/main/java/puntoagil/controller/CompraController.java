package puntoagil.controller;

import puntoagil.entity.Compra;
import puntoagil.service.CompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> listarTodas() {
        return compraService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Compra> registrar(@RequestBody Compra compra) {
        return ResponseEntity.ok(compraService.registrarCompra(compra));
    }
}