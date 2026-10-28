// dto/UsuarioUpdateRequest.java
package puntoagil.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import puntoagil.entity.Usuario;

public record UsuarioUpdateRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String email,
        @NotNull Usuario.Rol rol) {}