package puntoagil.dto;

import puntoagil.entity.MetodoPago;
import puntoagil.entity.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id, LocalDateTime fecha, BigDecimal total,
        UsuarioResumen usuario, List<DetalleResponse> detalles, List<PagoResponse> pagos) {

    public record UsuarioResumen(Long id, String nombre) {}
    public record ProductoResumen(Long id, String nombre) {}
    public record DetalleResponse(Long id, ProductoResumen producto, Integer cantidad,
                                  BigDecimal precioUnitario, BigDecimal subtotal,
                                  BigDecimal costoUnitario) {}
    public record PagoResponse(Long id, Long metodoPagoId, String metodoPago,
                               MetodoPago.Tipo tipo, BigDecimal monto, String referencia) {}

    // verCostos = true solo para ADMIN
    public static VentaResponse desde(Venta v, boolean verCostos) {
        return new VentaResponse(
                v.getId(), v.getFecha(), v.getTotal(),
                new UsuarioResumen(v.getUsuario().getId(), v.getUsuario().getNombre()),
                v.getDetalles().stream().map(d -> new DetalleResponse(
                        d.getId(),
                        new ProductoResumen(d.getProducto().getId(), d.getProducto().getNombre()),
                        d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal(),
                        verCostos ? d.getCostoUnitario() : null)).toList(),
                v.getPagos().stream().map(p -> new PagoResponse(
                        p.getId(), p.getMetodoPago().getId(), p.getMetodoPago().getNombre(),
                        p.getMetodoPago().getTipo(), p.getMonto(), p.getReferencia())).toList());
    }
}
