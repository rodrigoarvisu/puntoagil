package puntoagil.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import puntoagil.entity.DetalleVenta;
import puntoagil.entity.Producto;
import puntoagil.entity.Venta;
import puntoagil.repository.ProductoRepository;
import puntoagil.repository.VentaRepository;

import javax.lang.model.type.DeclaredType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Venta registrarVenta(Venta venta) {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado con id: " + detalle.getProducto().getId()));

            // Validación de negocio: no vender más de lo que hay en stock
            if (producto.getStock() < detalle.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para " + producto.getNombre() +
                                ". Disponible: " + producto.getStock() + ", solicitado: " + detalle.getCantidad());
            }

            // Calcular subtotal de la línea
            BigDecimal subtotal = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);

            // Descontar stock
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            total = total.add(subtotal);
        }
        venta.setTotal(total);
        venta.setFecha(LocalDateTime.now());

        return ventaRepository.save(venta);
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));
    }

    public List<Venta> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
}
