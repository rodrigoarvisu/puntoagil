package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puntoagil.entity.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
}
