package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import puntoagil.entity.MetodoPago;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByActivoTrueOrderByOrdenAscNombreAsc();
    List<MetodoPago> findAllByOrderByOrdenAscNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    long countByActivoTrue();
    long countByTipoAndActivoTrue(MetodoPago.Tipo tipo);
}
