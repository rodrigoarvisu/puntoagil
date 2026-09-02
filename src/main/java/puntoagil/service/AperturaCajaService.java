package puntoagil.service;

import puntoagil.entity.AperturaCaja;
import puntoagil.entity.Usuario;
import puntoagil.repository.AperturaCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AperturaCajaService {

    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;

    @Transactional
    public AperturaCaja abrirCaja(Usuario usuario, BigDecimal montoInicial) {
        if (aperturaCajaRepository.findByCerradaFalse().isPresent()) {
            throw new IllegalStateException("Ya existe una caja abierta. Debes cerrarla antes de abrir una nueva.");
        }

        AperturaCaja apertura = new AperturaCaja();
        apertura.setUsuario(usuario);
        apertura.setFecha(LocalDateTime.now());
        apertura.setMontoInicial(montoInicial);
        apertura.setCerrada(false);

        return aperturaCajaRepository.save(apertura);
    }

    public Optional<AperturaCaja> obtenerAperturaActiva() {
        return aperturaCajaRepository.findByCerradaFalse();
    }

    public AperturaCaja obtenerAperturaActivaObligatoria() {
        return aperturaCajaRepository.findByCerradaFalse()
                .orElseThrow(() -> new IllegalStateException("No hay una caja abierta. Debes abrir caja antes de vender."));
    }

    @Transactional
    public void cerrarAperturaActiva() {
        AperturaCaja apertura = obtenerAperturaActivaObligatoria();
        apertura.setCerrada(true);
        aperturaCajaRepository.save(apertura);
    }
}