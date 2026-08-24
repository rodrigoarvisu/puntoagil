package puntoagil.service;

import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import puntoagil.entity.Compra;
import puntoagil.entity.DetalleCompra;
import puntoagil.entity.Producto;
import puntoagil.repository.CompraRepository;
import puntoagil.repository.ProductoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Compra registrarCompra(Compra compra) {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCompra detalle : compra.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado con id: " + detalle.getProducto().getId()));

            // Calcular subtotal de la línea
            BigDecimal subtotal = detalle.getCostoUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setCompra(compra);

            // Actualizar stock y costo del producto
            producto.setStock(producto.getStock() + detalle.getCantidad());
            producto.setCosto(detalle.getCostoUnitario());
            productoRepository.save(producto);

            total = total.add(subtotal);
        }
        compra.setTotal(total);
        compra.setFecha(LocalDateTime.now());

        return compraRepository.save(compra);
    }

    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    public Compra buscarPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada con id: " + id));
    }
}
