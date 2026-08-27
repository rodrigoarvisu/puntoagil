package puntoagil.dto;

public record LoginResponse (String token, Long id, String nombre, String rol) {}
