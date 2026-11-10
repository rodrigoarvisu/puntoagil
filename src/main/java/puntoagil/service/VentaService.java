package puntoagil.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import puntoagil.dto.VentaRequest;
import puntoagil.entity.*;
import puntoagil.exception.RecursoNoEncontradoException;
import puntoagil.exception.ReglaNegocioException;
import puntoagil.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final AperturaCajaService aperturaCajaService;

    public VentaService(VentaRepository ventaRepository,
                        ProductoRepository productoRepository,
                        UsuarioRepository usuarioRepository,
                        MetodoPagoRepository metodoPagoRepository,
                        AperturaCajaService aperturaCajaService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.aperturaCajaService = aperturaCajaService;
    }

    @Transactional
    public Venta registrarVenta(VentaRequest req, String emailUsuario) {
        // Reintento del mismo envío: devuelve la venta ya registrada
        if (req.claveIdempotencia() != null && !req.claveIdempotencia().isBlank()) {
            Optional<Venta> existente = ventaRepository.findByClaveIdempotencia(req.claveIdempotencia());
            if (existente.isPresent()) return existente.get();
        }

        aperturaCajaService.obtenerAperturaActivaObligatoria();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Une líneas repetidas del mismo producto y las ordena por id (evita bloqueos cruzados)
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (VentaRequest.DetalleRequest d : req.detalles()) {
            cantidades.merge(d.productoId(), d.cantidad(), Integer::sum);
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        if (req.claveIdempotencia() != null && !req.claveIdempotencia().isBlank()) {
            venta.setClaveIdempotencia(req.claveIdempotencia());
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> e : new TreeMap<>(cantidades).entrySet()) {
            Producto producto = productoRepository.findById(e.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
            int cantidad = e.getValue();

            // Descuento atómico: si no alcanza el stock, no descuenta nada
            if (productoRepository.descontarStock(producto.getId(), cantidad) == 0) {
                throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre());
            }

            // El precio sale de la BD, nunca del cliente  <-- ajusta getPrecio() a tu campo
            BigDecimal precio = producto.getPrecioVenta().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setCostoUnitario(producto.getCosto());
            detalle.setSubtotal(subtotal);
            venta.getDetalles().add(detalle);

            total = total.add(subtotal);
        }

        if (total.signum() <= 0) {
            throw new ReglaNegocioException("El total de la venta debe ser mayor a cero");
        }
        venta.setTotal(total);

        BigDecimal sumaPagos = BigDecimal.ZERO;
        for (VentaRequest.PagoRequest pr : req.pagos()) {
            MetodoPago metodo = metodoPagoRepository.findById(pr.metodoPagoId())
                    .orElseThrow(() -> new ReglaNegocioException("Método de pago no válido"));
            if (!metodo.isActivo()) {
                throw new ReglaNegocioException("El método de pago " + metodo.getNombre() + " no está disponible");
            }
            String referencia = pr.referencia() == null ? null : pr.referencia().trim();
            if (metodo.isRequiereReferencia() && (referencia == null || referencia.isEmpty())) {
                throw new ReglaNegocioException("El método " + metodo.getNombre() + " requiere una referencia");
            }

            PagoVenta pago = new PagoVenta();
            pago.setVenta(venta);
            pago.setMetodoPago(metodo);
            pago.setMonto(pr.monto().setScale(2, RoundingMode.HALF_UP));
            pago.setReferencia(referencia == null || referencia.isEmpty() ? null : referencia);
            venta.getPagos().add(pago);

            sumaPagos = sumaPagos.add(pago.getMonto());
        }

        if (sumaPagos.compareTo(total) != 0) {
            throw new ReglaNegocioException(
                    "La suma de los pagos ($" + sumaPagos + ") no coincide con el total ($" + total + ")");
        }

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Venta> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
}