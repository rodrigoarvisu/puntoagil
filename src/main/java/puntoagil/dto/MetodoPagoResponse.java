package puntoagil.dto;

import puntoagil.entity.MetodoPago;

public record MetodoPagoResponse(Long id, String nombre, MetodoPago.Tipo tipo,
                                 boolean requiereReferencia, boolean activo, int orden) {
    public static MetodoPagoResponse desde(MetodoPago m) {
        return new MetodoPagoResponse(m.getId(), m.getNombre(), m.getTipo(),
                m.isRequiereReferencia(), m.isActivo(), m.getOrden());
    }
}
