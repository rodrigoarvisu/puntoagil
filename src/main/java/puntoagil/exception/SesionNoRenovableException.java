package puntoagil.exception;

public class SesionNoRenovableException extends RuntimeException {
    public SesionNoRenovableException(String mensaje) { super(mensaje); }
}