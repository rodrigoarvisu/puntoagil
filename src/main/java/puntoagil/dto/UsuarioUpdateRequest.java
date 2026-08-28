// dto/UsuarioUpdateRequest.java
package puntoagil.dto;
import puntoagil.entity.Usuario;

public record UsuarioUpdateRequest(String nombre, String email, Usuario.Rol rol) {}