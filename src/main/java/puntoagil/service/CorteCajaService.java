package puntoagil.service;

import puntoagil.entity.AperturaCaja;
import puntoagil.entity.CorteCaja;
import puntoagil.entity.Usuario;
import puntoagil.entity.Venta;
import puntoagil.repository.AperturaCajaRepository;
import puntoagil.repository.CorteCajaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CorteCajaService {

    private CorteCajaRepository corteCajaRepository;

    private VentaService ventaService;

    private AperturaCajaService aperturaCajaService;

    public CorteCajaService(CorteCajaRepository corteCajaRepository,
                            VentaService ventaService,
                            AperturaCajaService aperturaCajaService) {
        this.corteCajaRepository = corteCajaRepository;
        this.ventaService = ventaService;
        this.aperturaCajaService = aperturaCajaService;
    }

    @Transactional
    public CorteCaja generarCorte(Usuario usuario, LocalDateTime inicio, LocalDateTime fin, BigDecimal efectivoContado) {
        AperturaCaja apertura = aperturaCajaService.obtenerAperturaActivaObligatoria();

        List<Venta> ventasDelPeriodo = ventaService.buscarPorRangoFechas(inicio, fin);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalVentasEfectivo = BigDecimal.ZERO;
        BigDecimal totalUtilidad = BigDecimal.ZERO;

        for (Venta venta : ventasDelPeriodo) {
            totalVentas = totalVentas.add(venta.getTotal());

            if (venta.getMetodoPago() == Venta.MetodoPago.EFECTIVO) {
                totalVentasEfectivo = totalVentasEfectivo.add(venta.getTotal());
            }

            for (var detalle : venta.getDetalles()) {
                BigDecimal costoLinea = detalle.getProducto().getCosto()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));
                BigDecimal utilidadLinea = detalle.getSubtotal().subtract(costoLinea);
                totalUtilidad = totalUtilidad.add(utilidadLinea);
            }
        }

        BigDecimal efectivoEsperado = apertura.getMontoInicial().add(totalVentasEfectivo);

        CorteCaja corte = new CorteCaja();
        corte.setUsuario(usuario);
        corte.setFecha(LocalDateTime.now());
        corte.setTotalVentas(totalVentas);
        corte.setTotalUtilidad(totalUtilidad);
        corte.setEfectivoEsperado(efectivoEsperado);
        corte.setEfectivoContado(efectivoContado);
        corte.setDiferencia(efectivoContado.subtract(efectivoEsperado));

        CorteCaja corteGuardado = corteCajaRepository.save(corte);

        aperturaCajaService.cerrarAperturaActiva(); // cierra la caja automáticamente al generar el corte

        return corteGuardado;
    }

    public List<CorteCaja> listarTodos() {
        return corteCajaRepository.findAll();
    }

    public CorteCaja buscarPorId(Long id) {
        return corteCajaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corte de caja no encontrado con id: " + id));
    }
}