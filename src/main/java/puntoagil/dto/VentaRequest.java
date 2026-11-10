package puntoagil.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record VentaRequest(
        @NotEmpty @Size(max = 100) @Valid List<DetalleRequest> detalles,
        @NotEmpty @Size(max = 5) @Valid List<PagoRequest> pagos,
        @Size(max = 64) String claveIdempotencia) {

    public record DetalleRequest(
            @NotNull Long productoId,
            @NotNull @Min(1) @Max(10000) Integer cantidad) {}

    public record PagoRequest(
            @NotNull Long metodoPagoId,
            @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal monto,
            @Size(max = 100) String referencia) {}
}
