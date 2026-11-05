package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.DiaSemana;
import ar.edu.um.isa.oncall.domain.enumeration.TipoVentana;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Periodo programado durante el cual un servicio esta en mantenimiento.
 *
 * Para una ventana UNICA se usan inicio y fin. Para una ventana SEMANAL
 * se usan diaSemana, minutoInicio y minutoFin en la zonaHoraria indicada.
 * Si minutoFin es menor o igual que minutoInicio, el intervalo termina
 * al dia siguiente.
 */
@Entity
@Table(name = "ventana_de_mantenimiento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentanaDeMantenimiento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "nombre", length = 80, nullable = false, unique = true)
    private String nombre;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoVentana tipo;

    @NotNull
    @Size(max = 50)
    @Column(name = "zona_horaria", length = 50, nullable = false)
    private String zonaHoraria;

    @Column(name = "inicio")
    private Instant inicio;

    @Column(name = "fin")
    private Instant fin;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DiaSemana diaSemana;

    @Min(value = 0)
    @Max(value = 1439)
    @Column(name = "minuto_inicio")
    private Integer minutoInicio;

    @Min(value = 0)
    @Max(value = 1439)
    @Column(name = "minuto_fin")
    private Integer minutoFin;

    @Column(name = "vigente_desde")
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @NotNull
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ventana")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "servicio", "ventana" }, allowSetters = true)
    private Set<ServicioEnVentana> servicioEnVentanas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ventana")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ventana" }, allowSetters = true)
    private Set<ExcepcionDeVentana> excepcions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VentanaDeMantenimiento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public VentanaDeMantenimiento nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public VentanaDeMantenimiento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoVentana getTipo() {
        return this.tipo;
    }

    public VentanaDeMantenimiento tipo(TipoVentana tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoVentana tipo) {
        this.tipo = tipo;
    }

    public String getZonaHoraria() {
        return this.zonaHoraria;
    }

    public VentanaDeMantenimiento zonaHoraria(String zonaHoraria) {
        this.setZonaHoraria(zonaHoraria);
        return this;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public Instant getInicio() {
        return this.inicio;
    }

    public VentanaDeMantenimiento inicio(Instant inicio) {
        this.setInicio(inicio);
        return this;
    }

    public void setInicio(Instant inicio) {
        this.inicio = inicio;
    }

    public Instant getFin() {
        return this.fin;
    }

    public VentanaDeMantenimiento fin(Instant fin) {
        this.setFin(fin);
        return this;
    }

    public void setFin(Instant fin) {
        this.fin = fin;
    }

    public DiaSemana getDiaSemana() {
        return this.diaSemana;
    }

    public VentanaDeMantenimiento diaSemana(DiaSemana diaSemana) {
        this.setDiaSemana(diaSemana);
        return this;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Integer getMinutoInicio() {
        return this.minutoInicio;
    }

    public VentanaDeMantenimiento minutoInicio(Integer minutoInicio) {
        this.setMinutoInicio(minutoInicio);
        return this;
    }

    public void setMinutoInicio(Integer minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public Integer getMinutoFin() {
        return this.minutoFin;
    }

    public VentanaDeMantenimiento minutoFin(Integer minutoFin) {
        this.setMinutoFin(minutoFin);
        return this;
    }

    public void setMinutoFin(Integer minutoFin) {
        this.minutoFin = minutoFin;
    }

    public LocalDate getVigenteDesde() {
        return this.vigenteDesde;
    }

    public VentanaDeMantenimiento vigenteDesde(LocalDate vigenteDesde) {
        this.setVigenteDesde(vigenteDesde);
        return this;
    }

    public void setVigenteDesde(LocalDate vigenteDesde) {
        this.vigenteDesde = vigenteDesde;
    }

    public LocalDate getVigenteHasta() {
        return this.vigenteHasta;
    }

    public VentanaDeMantenimiento vigenteHasta(LocalDate vigenteHasta) {
        this.setVigenteHasta(vigenteHasta);
        return this;
    }

    public void setVigenteHasta(LocalDate vigenteHasta) {
        this.vigenteHasta = vigenteHasta;
    }

    public Boolean getActiva() {
        return this.activa;
    }

    public VentanaDeMantenimiento activa(Boolean activa) {
        this.setActiva(activa);
        return this;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Set<ServicioEnVentana> getServicioEnVentanas() {
        return this.servicioEnVentanas;
    }

    public void setServicioEnVentanas(Set<ServicioEnVentana> servicioEnVentanas) {
        if (this.servicioEnVentanas != null) {
            this.servicioEnVentanas.forEach(i -> i.setVentana(null));
        }
        if (servicioEnVentanas != null) {
            servicioEnVentanas.forEach(i -> i.setVentana(this));
        }
        this.servicioEnVentanas = servicioEnVentanas;
    }

    public VentanaDeMantenimiento servicioEnVentanas(Set<ServicioEnVentana> servicioEnVentanas) {
        this.setServicioEnVentanas(servicioEnVentanas);
        return this;
    }

    public VentanaDeMantenimiento addServicioEnVentana(ServicioEnVentana servicioEnVentana) {
        this.servicioEnVentanas.add(servicioEnVentana);
        servicioEnVentana.setVentana(this);
        return this;
    }

    public VentanaDeMantenimiento removeServicioEnVentana(ServicioEnVentana servicioEnVentana) {
        this.servicioEnVentanas.remove(servicioEnVentana);
        servicioEnVentana.setVentana(null);
        return this;
    }

    public Set<ExcepcionDeVentana> getExcepcions() {
        return this.excepcions;
    }

    public void setExcepcions(Set<ExcepcionDeVentana> excepcionDeVentanas) {
        if (this.excepcions != null) {
            this.excepcions.forEach(i -> i.setVentana(null));
        }
        if (excepcionDeVentanas != null) {
            excepcionDeVentanas.forEach(i -> i.setVentana(this));
        }
        this.excepcions = excepcionDeVentanas;
    }

    public VentanaDeMantenimiento excepcions(Set<ExcepcionDeVentana> excepcionDeVentanas) {
        this.setExcepcions(excepcionDeVentanas);
        return this;
    }

    public VentanaDeMantenimiento addExcepcion(ExcepcionDeVentana excepcionDeVentana) {
        this.excepcions.add(excepcionDeVentana);
        excepcionDeVentana.setVentana(this);
        return this;
    }

    public VentanaDeMantenimiento removeExcepcion(ExcepcionDeVentana excepcionDeVentana) {
        this.excepcions.remove(excepcionDeVentana);
        excepcionDeVentana.setVentana(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentanaDeMantenimiento)) {
            return false;
        }
        return getId() != null && getId().equals(((VentanaDeMantenimiento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentanaDeMantenimiento{" +
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
