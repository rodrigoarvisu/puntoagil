package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puntoagil.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
