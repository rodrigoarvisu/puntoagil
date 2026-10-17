package puntoagil.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "negocio")
public class Negocio {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    @Column(length = 13)
    private String rfc;

    @Column(length = 15)
    private String telefono;

    @Column(length = 120)
    private String email;

    @Column(name = "sitio_web", length = 150)
    private String sitioWeb;

    @Column(length = 150)
    private String calle;

    @Column(length = 100)
    private String colonia;

    @Column(name = "codigo_postal", length = 5)
    private String codigoPostal;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String estado;

    public Negocio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getColonia() { return colonia; }
    public void setColonia(String colonia) { this.colonia = colonia; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}