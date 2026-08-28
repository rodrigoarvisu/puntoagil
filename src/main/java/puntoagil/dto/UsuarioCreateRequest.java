// dto/UsuarioCreateRequest.java
package puntoagil.dto;
import puntoagil.entity.Usuario;

public record UsuarioCreateRequest(String nombre, String email, String password, Usuario.Rol rol) {}