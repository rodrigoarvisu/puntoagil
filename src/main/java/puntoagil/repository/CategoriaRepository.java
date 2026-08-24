package puntoagil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puntoagil.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
