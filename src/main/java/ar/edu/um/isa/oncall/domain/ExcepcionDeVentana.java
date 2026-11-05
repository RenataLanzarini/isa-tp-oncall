package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.TipoExcepcionVentana;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Excepcion concreta de una ventana. Una cancelacion elimina una
 * ocurrencia; una inclusion agrega una ocurrencia extraordinaria.
 */
@Entity
@Table(name = "excepcion_de_ventana")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExcepcionDeVentana implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoExcepcionVentana tipo;

    @NotNull
    @Column(name = "desde", nullable = false)
    private Instant desde;

    @NotNull
    @Column(name = "hasta", nullable = false)
    private Instant hasta;

    @Size(max = 255)
    @Column(name = "motivo", length = 255)
    private String motivo;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "servicioEnVentanas", "excepcions" }, allowSetters = true)
    private VentanaDeMantenimiento ventana;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExcepcionDeVentana id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoExcepcionVentana getTipo() {
        return this.tipo;
    }

    public ExcepcionDeVentana tipo(TipoExcepcionVentana tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoExcepcionVentana tipo) {
        this.tipo = tipo;
    }

    public Instant getDesde() {
        return this.desde;
    }

    public ExcepcionDeVentana desde(Instant desde) {
        this.setDesde(desde);
        return this;
    }

    public void setDesde(Instant desde) {
        this.desde = desde;
    }

    public Instant getHasta() {
        return this.hasta;
    }

    public ExcepcionDeVentana hasta(Instant hasta) {
        this.setHasta(hasta);
        return this;
    }

    public void setHasta(Instant hasta) {
        this.hasta = hasta;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public ExcepcionDeVentana motivo(String motivo) {
        this.setMotivo(motivo);
        return this;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public VentanaDeMantenimiento getVentana() {
        return this.ventana;
    }

    public void setVentana(VentanaDeMantenimiento ventanaDeMantenimiento) {
        this.ventana = ventanaDeMantenimiento;
    }

    public ExcepcionDeVentana ventana(VentanaDeMantenimiento ventanaDeMantenimiento) {
        this.setVentana(ventanaDeMantenimiento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExcepcionDeVentana)) {
            return false;
        }
        return getId() != null && getId().equals(((ExcepcionDeVentana) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExcepcionDeVentana{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", desde='" + getDesde() + "'" +
            ", hasta='" + getHasta() + "'" +
            ", motivo='" + getMotivo() + "'" +
            "}";
    }
}
