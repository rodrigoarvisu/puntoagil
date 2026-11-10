package puntoagil.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "metodos_pago")
public class MetodoPago {

    public enum Tipo { EFECTIVO, TARJETA, TRANSFERENCIA, OTRO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Tipo tipo;

    @Column(name = "requiere_referencia", nullable = false)
    private boolean requiereReferencia;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private int orden;
}
