package puntoagil.service;

import puntoagil.entity.CorteCaja;
import puntoagil.entity.Usuario;
import puntoagil.entity.Venta;
import puntoagil.repository.CorteCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CorteCajaService {

    @Autowired
    private CorteCajaRepository corteCajaRepository;

    @Autowired
    private VentaService ventaService;

    @Transactional
    public CorteCaja generarCorte(Usuario usuario, LocalDateTime inicio, LocalDateTime fin, BigDecimal efectivoContado) {
        List<Venta> ventasDelPeriodo = ventaService.buscarPorRangoFechas(inicio, fin);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalUtilidad = BigDecimal.ZERO;

        for (Venta venta : ventasDelPeriodo) {
            totalVentas = totalVentas.add(venta.getTotal());

            for (var detalle : venta.getDetalles()) {
                BigDecimal costoLinea = detalle.getProducto().getCosto()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));
                BigDecimal utilidadLinea = detalle.getSubtotal().subtract(costoLinea);
                totalUtilidad = totalUtilidad.add(utilidadLinea);
            }
        }

        CorteCaja corte = new CorteCaja();
        corte.setUsuario(usuario);
        corte.setFecha(LocalDateTime.now());
        corte.setTotalVentas(totalVentas);
        corte.setTotalUtilidad(totalUtilidad);
        corte.setEfectivoEsperado(totalVentas);
        corte.setEfectivoContado(efectivoContado);
        corte.setDiferencia(efectivoContado.subtract(totalVentas));

        return corteCajaRepository.save(corte);
    }

    public List<CorteCaja> listarTodos() {
        return corteCajaRepository.findAll();
    }

    public CorteCaja buscarPorId(Long id) {
        return corteCajaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corte de caja no encontrado con id: " + id));
    }
}