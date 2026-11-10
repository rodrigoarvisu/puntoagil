package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import puntoagil.entity.PagoVenta;

@Repository
public interface PagoVentaRepository extends JpaRepository<PagoVenta, Long> {
    boolean existsByMetodoPagoId(Long metodoPagoId);
}
