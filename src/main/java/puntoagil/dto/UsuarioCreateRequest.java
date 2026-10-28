// dto/UsuarioCreateRequest.java
package puntoagil.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import puntoagil.entity.Usuario;

public record UsuarioCreateRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 10, max = 72) String password,
        @NotNull Usuario.Rol rol) {}