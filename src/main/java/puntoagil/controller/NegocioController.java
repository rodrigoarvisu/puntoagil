package puntoagil.controller;

import org.springframework.web.bind.annotation.*;
import puntoagil.entity.Negocio;
import puntoagil.service.NegocioService;

@RestController
@RequestMapping("/api/negocio")
public class NegocioController {
    private NegocioService negocioService;

    public NegocioController(NegocioService negocioService) {
        this.negocioService = negocioService;
    }

    @GetMapping
    public Negocio obtener() {
        return negocioService.obtener();
    }

    @PutMapping
    public Negocio guardar(@RequestBody Negocio negocio) {
        return negocioService.guardar(negocio);
    }
}
