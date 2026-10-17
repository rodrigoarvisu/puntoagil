package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import puntoagil.entity.Negocio;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Long> {
}
