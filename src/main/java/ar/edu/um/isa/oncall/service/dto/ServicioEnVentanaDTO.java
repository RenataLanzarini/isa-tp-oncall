package ar.edu.um.isa.oncall.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.ServicioEnVentana} entity.
 */
@Schema(description = "Asignacion explicita de una ventana de mantenimiento a un servicio.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServicioEnVentanaDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean activo;

    @Size(max = 255)
    private String nota;

    @NotNull
    private ServicioDTO servicio;

    @NotNull
    private VentanaDeMantenimientoDTO ventana;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
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
        if (!(o instanceof ServicioEnVentanaDTO)) {
            return false;
        }

        ServicioEnVentanaDTO servicioEnVentanaDTO = (ServicioEnVentanaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, servicioEnVentanaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServicioEnVentanaDTO{" +
            "id=" + getId() +
            ", activo='" + getActivo() + "'" +
            ", nota='" + getNota() + "'" +
            ", servicio=" + getServicio() +
            ", ventana=" + getVentana() +
            "}";
    }
}
