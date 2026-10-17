package puntoagil.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import puntoagil.entity.Negocio;
import puntoagil.repository.NegocioRepository;

import java.util.regex.Pattern;

@Service
public class NegocioService {
    private static final Long ID_UNICO = 1L;

    private static final Pattern RFC = Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z\\d]{3}$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern CODIGO_POSTAL = Pattern.compile("^\\d{5}$");
    private static final Pattern SITIO_WEB = Pattern.compile("^(https?://)?[^\\s/]+\\.[^\\s/]+(/\\S*)?$", Pattern.CASE_INSENSITIVE);

    private NegocioRepository negocioRepository;

    public NegocioService(NegocioRepository negocioRepository){
        this.negocioRepository = negocioRepository;
    }

    public Negocio obtener() {
        return negocioRepository.findById(ID_UNICO).orElseGet(Negocio::new);
    }

    @Transactional
    public Negocio guardar(Negocio datos) {
        limpiar(datos);
        validar(datos);
        datos.setId(ID_UNICO);
        return negocioRepository.save(datos);
    }

    private void limpiar(Negocio n) {
        n.setNombre(limpio(n.getNombre()));
        n.setRazonSocial(limpio(n.getRazonSocial()));
        n.setRfc(limpio(n.getRfc()) == null ? null : limpio(n.getRfc()).toUpperCase());
        n.setTelefono(limpio(n.getTelefono()));
        n.setEmail(limpio(n.getEmail()));
        n.setSitioWeb(limpio(n.getSitioWeb()));
        n.setCalle(limpio(n.getCalle()));
        n.setColonia(limpio(n.getColonia()));
        n.setCodigoPostal(limpio(n.getCodigoPostal()));
        n.setCiudad(limpio(n.getCiudad()));
        n.setEstado(limpio(n.getEstado()));
    }

    private String limpio(String valor) {
        if (valor == null) return null;
        String recortado = valor.trim();
        return recortado.isEmpty() ? null : recortado;
    }

    private void validar(Negocio n) {
        if (n.getNombre() == null) {
            error("El nombre del negocio es obligatorio.");
        }
        if (n.getRfc() != null && !RFC.matcher(n.getRfc()).matches()) {
            error("RFC no válido.");
        }
        if (n.getTelefono() != null && n.getTelefono().replaceAll("\\D", "").length() != 10) {
            error("El teléfono debe tener 10 dígitos.");
        }
        if (n.getEmail() != null && !EMAIL.matcher(n.getEmail()).matches()) {
            error("Correo electrónico no válido.");
        }
        if (n.getSitioWeb() != null && !SITIO_WEB.matcher(n.getSitioWeb()).matches()) {
            error("Sitio web no válido.");
        }
        if (n.getCodigoPostal() != null && !CODIGO_POSTAL.matcher(n.getCodigoPostal()).matches()) {
            error("El código postal debe tener 5 dígitos.");
        }
        validarLongitud(n.getNombre(), 100, "El nombre");
        validarLongitud(n.getRazonSocial(), 150, "La razón social");
        validarLongitud(n.getCalle(), 150, "La calle");
        validarLongitud(n.getColonia(), 100, "La colonia");
        validarLongitud(n.getCiudad(), 100, "La ciudad");
        validarLongitud(n.getEstado(), 100, "El estado");
        validarLongitud(n.getEmail(), 120, "El correo");
        validarLongitud(n.getSitioWeb(), 150, "El sitio web");
    }

    private void validarLongitud(String valor, int maximo, String campo) {
        if (valor != null && valor.length() > maximo) {
            error(campo + " no puede superar " + maximo + " caracteres.");
        }
    }

    private void error(String mensaje) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}
