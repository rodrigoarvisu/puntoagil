package puntoagil.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import puntoagil.dto.MetodoPagoRequest;
import puntoagil.entity.MetodoPago;
import puntoagil.exception.ConflictoException;
import puntoagil.exception.RecursoNoEncontradoException;
import puntoagil.exception.ReglaNegocioException;
import puntoagil.repository.MetodoPagoRepository;
import puntoagil.repository.PagoVentaRepository;

import java.util.List;

@Service
@Transactional
public class MetodoPagoService {

    private final MetodoPagoRepository repo;
    private final PagoVentaRepository pagoRepo;

    public MetodoPagoService(MetodoPagoRepository repo, PagoVentaRepository pagoRepo) {
        this.repo = repo;
        this.pagoRepo = pagoRepo;
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> listarActivos() {
        return repo.findByActivoTrueOrderByOrdenAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> listarTodos() {
        return repo.findAllByOrderByOrdenAscNombreAsc();
    }

    public MetodoPago crear(MetodoPagoRequest r) {
        String nombre = r.nombre().trim();
        if (repo.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un método de pago con ese nombre");
        }
        MetodoPago m = new MetodoPago();
        m.setNombre(nombre);
        m.setTipo(r.tipo());
        m.setRequiereReferencia(r.requiereReferencia());
        m.setActivo(true);
        m.setOrden((int) repo.count() + 1);
        return repo.save(m);
    }

    public MetodoPago actualizar(Long id, MetodoPagoRequest r) {
        MetodoPago m = buscar(id);
        String nombre = r.nombre().trim();
        if (repo.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe un método de pago con ese nombre");
        }
        if (m.getTipo() != r.tipo()) {
            if (pagoRepo.existsByMetodoPagoId(id)) {
                throw new ReglaNegocioException("No puedes cambiar el tipo de un método que ya tiene ventas registradas");
            }
            if (m.getTipo() == MetodoPago.Tipo.EFECTIVO && m.isActivo()) {
                exigirOtroEfectivoActivo();
            }
        }
        m.setNombre(nombre);
        m.setTipo(r.tipo());
        m.setRequiereReferencia(r.requiereReferencia());
        return m;
    }

    public MetodoPago cambiarEstado(Long id, boolean activo) {
        MetodoPago m = buscar(id);
        if (m.isActivo() && !activo) {
            if (repo.countByActivoTrue() <= 1) {
                throw new ReglaNegocioException("Debe haber al menos un método de pago activo");
            }
            if (m.getTipo() == MetodoPago.Tipo.EFECTIVO) {
                exigirOtroEfectivoActivo();
            }
        }
        m.setActivo(activo);
        return m;
    }

    private void exigirOtroEfectivoActivo() {
        if (repo.countByTipoAndActivoTrue(MetodoPago.Tipo.EFECTIVO) <= 1) {
            throw new ReglaNegocioException("Debe quedar al menos un método de tipo efectivo activo");
        }
    }

    private MetodoPago buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado"));
    }
}