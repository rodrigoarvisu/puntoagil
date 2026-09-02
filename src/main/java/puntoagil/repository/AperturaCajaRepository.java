package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puntoagil.entity.AperturaCaja;
import java.util.Optional;

public interface AperturaCajaRepository extends JpaRepository<AperturaCaja, Long> {
    Optional<AperturaCaja> findByCerradaFalse();
}