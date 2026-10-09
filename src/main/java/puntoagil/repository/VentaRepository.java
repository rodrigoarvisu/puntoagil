package puntoagil.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import puntoagil.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}