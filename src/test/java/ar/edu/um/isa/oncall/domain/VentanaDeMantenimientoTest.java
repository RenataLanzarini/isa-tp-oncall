package ar.edu.um.isa.oncall.domain;

import static ar.edu.um.isa.oncall.domain.ExcepcionDeVentanaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.ServicioEnVentanaTestSamples.*;
import static ar.edu.um.isa.oncall.domain.VentanaDeMantenimientoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.isa.oncall.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VentanaDeMantenimientoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VentanaDeMantenimiento.class);
        VentanaDeMantenimiento ventanaDeMantenimiento1 = getVentanaDeMantenimientoSample1();
        VentanaDeMantenimiento ventanaDeMantenimiento2 = new VentanaDeMantenimiento();
        assertThat(ventanaDeMantenimiento1).isNotEqualTo(ventanaDeMantenimiento2);

        ventanaDeMantenimiento2.setId(ventanaDeMantenimiento1.getId());
        assertThat(ventanaDeMantenimiento1).isEqualTo(ventanaDeMantenimiento2);

        ventanaDeMantenimiento2 = getVentanaDeMantenimientoSample2();
        assertThat(ventanaDeMantenimiento1).isNotEqualTo(ventanaDeMantenimiento2);
    }

    @Test
    void servicioEnVentanaTest() {
        VentanaDeMantenimiento ventanaDeMantenimiento = getVentanaDeMantenimientoRandomSampleGenerator();
        ServicioEnVentana servicioEnVentanaBack = getServicioEnVentanaRandomSampleGenerator();

        ventanaDeMantenimiento.addServicioEnVentana(servicioEnVentanaBack);
        assertThat(ventanaDeMantenimiento.getServicioEnVentanas()).containsOnly(servicioEnVentanaBack);
        assertThat(servicioEnVentanaBack.getVentana()).isEqualTo(ventanaDeMantenimiento);

        ventanaDeMantenimiento.removeServicioEnVentana(servicioEnVentanaBack);
        assertThat(ventanaDeMantenimiento.getServicioEnVentanas()).doesNotContain(servicioEnVentanaBack);
        assertThat(servicioEnVentanaBack.getVentana()).isNull();

        ventanaDeMantenimiento.servicioEnVentanas(new HashSet<>(Set.of(servicioEnVentanaBack)));
        assertThat(ventanaDeMantenimiento.getServicioEnVentanas()).containsOnly(servicioEnVentanaBack);
        assertThat(servicioEnVentanaBack.getVentana()).isEqualTo(ventanaDeMantenimiento);

        ventanaDeMantenimiento.setServicioEnVentanas(new HashSet<>());
        assertThat(ventanaDeMantenimiento.getServicioEnVentanas()).doesNotContain(servicioEnVentanaBack);
        assertThat(servicioEnVentanaBack.getVentana()).isNull();
    }

    @Test
    void excepcionTest() {
        VentanaDeMantenimiento ventanaDeMantenimiento = getVentanaDeMantenimientoRandomSampleGenerator();
        ExcepcionDeVentana excepcionDeVentanaBack = getExcepcionDeVentanaRandomSampleGenerator();

        ventanaDeMantenimiento.addExcepcion(excepcionDeVentanaBack);
        assertThat(ventanaDeMantenimiento.getExcepcions()).containsOnly(excepcionDeVentanaBack);
        assertThat(excepcionDeVentanaBack.getVentana()).isEqualTo(ventanaDeMantenimiento);

        ventanaDeMantenimiento.removeExcepcion(excepcionDeVentanaBack);
        assertThat(ventanaDeMantenimiento.getExcepcions()).doesNotContain(excepcionDeVentanaBack);
        assertThat(excepcionDeVentanaBack.getVentana()).isNull();

        ventanaDeMantenimiento.excepcions(new HashSet<>(Set.of(excepcionDeVentanaBack)));
        assertThat(ventanaDeMantenimiento.getExcepcions()).containsOnly(excepcionDeVentanaBack);
        assertThat(excepcionDeVentanaBack.getVentana()).isEqualTo(ventanaDeMantenimiento);

        ventanaDeMantenimiento.setExcepcions(new HashSet<>());
        assertThat(ventanaDeMantenimiento.getExcepcions()).doesNotContain(excepcionDeVentanaBack);
        assertThat(excepcionDeVentanaBack.getVentana()).isNull();
    }
}
