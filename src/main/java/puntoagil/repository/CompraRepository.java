package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puntoagil.entity.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long> {
}
