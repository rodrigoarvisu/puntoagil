// dto/UsuarioResponse.java
package puntoagil.dto;
import puntoagil.entity.Usuario;

public record UsuarioResponse(Long id, String nombre, String email, String rol, boolean activo) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol().name(), u.isActivo());
    }
}