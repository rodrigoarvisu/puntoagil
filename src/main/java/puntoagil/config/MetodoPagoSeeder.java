package puntoagil.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import puntoagil.entity.MetodoPago;
import puntoagil.repository.MetodoPagoRepository;

@Component
public class MetodoPagoSeeder implements CommandLineRunner {

    private final MetodoPagoRepository repo;

    public MetodoPagoSeeder(MetodoPagoRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        crear("Efectivo", MetodoPago.Tipo.EFECTIVO, 1);
        crear("Tarjeta", MetodoPago.Tipo.TARJETA, 2);
        crear("Transferencia", MetodoPago.Tipo.TRANSFERENCIA, 3);
    }

    private void crear(String nombre, MetodoPago.Tipo tipo, int orden) {
        MetodoPago m = new MetodoPago();
        m.setNombre(nombre);
        m.setTipo(tipo);
        m.setActivo(true);
        m.setOrden(orden);
        repo.save(m);
    }
}
