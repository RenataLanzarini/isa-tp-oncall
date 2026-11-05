package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.TipoExcepcionVentana;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.ExcepcionDeVentana} entity.
 */
@Schema(
    description = "Excepcion concreta de una ventana. Una cancelacion elimina una\nocurrencia; una inclusion agrega una ocurrencia extraordinaria."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExcepcionDeVentanaDTO implements Serializable {

    private Long id;

    @NotNull
    private TipoExcepcionVentana tipo;

    @NotNull
    private Instant desde;

    @NotNull
    private Instant hasta;

    @Size(max = 255)
    private String motivo;

    @NotNull
    private VentanaDeMantenimientoDTO ventana;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoExcepcionVentana getTipo() {
        return tipo;
    }

    public void setTipo(TipoExcepcionVentana tipo) {
        this.tipo = tipo;
    }

    public Instant getDesde() {
        return desde;
    }

    public void setDesde(Instant desde) {
        this.desde = desde;
    }

    public Instant getHasta() {
        return hasta;
    }

    public void setHasta(Instant hasta) {
        this.hasta = hasta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public VentanaDeMantenimientoDTO getVentana() {
        return ventana;
    }

    public void setVentana(VentanaDeMantenimientoDTO ventana) {
        this.ventana = ventana;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExcepcionDeVentanaDTO)) {
            return false;
        }

        ExcepcionDeVentanaDTO excepcionDeVentanaDTO = (ExcepcionDeVentanaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, excepcionDeVentanaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExcepcionDeVentanaDTO{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", desde='" + getDesde() + "'" +
            ", hasta='" + getHasta() + "'" +
            ", motivo='" + getMotivo() + "'" +
            ", ventana=" + getVentana() +
            "}";
    }
}
