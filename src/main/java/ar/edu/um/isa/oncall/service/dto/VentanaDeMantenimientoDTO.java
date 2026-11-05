package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.DiaSemana;
import ar.edu.um.isa.oncall.domain.enumeration.TipoVentana;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.VentanaDeMantenimiento} entity.
 */
@Schema(
    description = "Periodo programado durante el cual un servicio esta en mantenimiento.\n\nPara una ventana UNICA se usan inicio y fin. Para una ventana SEMANAL\nse usan diaSemana, minutoInicio y minutoFin en la zonaHoraria indicada.\nSi minutoFin es menor o igual que minutoInicio, el intervalo termina\nal dia siguiente."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentanaDeMantenimientoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotNull
    private TipoVentana tipo;

    @NotNull
    @Size(max = 50)
    private String zonaHoraria;

    private Instant inicio;

    private Instant fin;

    private DiaSemana diaSemana;

    @Min(value = 0)
    @Max(value = 1439)
    private Integer minutoInicio;

    @Min(value = 0)
    @Max(value = 1439)
    private Integer minutoFin;

    private LocalDate vigenteDesde;

    private LocalDate vigenteHasta;

    @NotNull
    private Boolean activa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoVentana getTipo() {
        return tipo;
    }

    public void setTipo(TipoVentana tipo) {
        this.tipo = tipo;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public Instant getInicio() {
        return inicio;
    }

    public void setInicio(Instant inicio) {
        this.inicio = inicio;
    }

    public Instant getFin() {
        return fin;
    }

    public void setFin(Instant fin) {
        this.fin = fin;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Integer getMinutoInicio() {
        return minutoInicio;
    }

    public void setMinutoInicio(Integer minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public Integer getMinutoFin() {
        return minutoFin;
    }

    public void setMinutoFin(Integer minutoFin) {
        this.minutoFin = minutoFin;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }

    public void setVigenteDesde(LocalDate vigenteDesde) {
        this.vigenteDesde = vigenteDesde;
    }

    public LocalDate getVigenteHasta() {
        return vigenteHasta;
    }

    public void setVigenteHasta(LocalDate vigenteHasta) {
        this.vigenteHasta = vigenteHasta;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentanaDeMantenimientoDTO)) {
            return false;
        }

        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = (VentanaDeMantenimientoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ventanaDeMantenimientoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentanaDeMantenimientoDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", tipo='" + getTipo() + "'" +
            ", zonaHoraria='" + getZonaHoraria() + "'" +
            ", inicio='" + getInicio() + "'" +
            ", fin='" + getFin() + "'" +
            ", diaSemana='" + getDiaSemana() + "'" +
            ", minutoInicio=" + getMinutoInicio() +
            ", minutoFin=" + getMinutoFin() +
            ", vigenteDesde='" + getVigenteDesde() + "'" +
            ", vigenteHasta='" + getVigenteHasta() + "'" +
            ", activa='" + getActiva() + "'" +
            "}";
    }
}
