package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import puntoagil.entity.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockEquals(Integer stock);

    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.stock <= p.stockMinimo")
    List<Producto> findPorAgotarse();

    List<Producto> findByCategoriaId(Long categoriaId);
}
