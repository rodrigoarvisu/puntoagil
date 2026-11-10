package puntoagil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import puntoagil.entity.MetodoPago;

public record MetodoPagoRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotNull MetodoPago.Tipo tipo,
        boolean requiereReferencia) {}