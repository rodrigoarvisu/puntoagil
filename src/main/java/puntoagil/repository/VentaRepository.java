package puntoagil.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import puntoagil.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<Venta> findByClaveIdempotencia(String clave);
}