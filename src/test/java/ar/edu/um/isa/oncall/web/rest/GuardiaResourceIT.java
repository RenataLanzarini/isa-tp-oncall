package ar.edu.um.isa.oncall.web.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Equipo;
import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.TipoRotacion;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class GuardiaResourceIT {

    private static final Instant CONSULTA = Instant.parse("2026-11-05T12:00:00Z");

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void devuelveGuardiaNormal() throws Exception {
        Rotacion rotacion = persistirRotacion(true, "America/Argentina/Buenos_Aires");
        persistirTurno(rotacion, persistirUsuario("titular"), CONSULTA.minusSeconds(3600), CONSULTA.plusSeconds(3600), false);

        consultar(rotacion, CONSULTA)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("CON_COBERTURA"))
            .andExpect(jsonPath("$.responsables", hasSize(1)))
            .andExpect(jsonPath("$.responsables[0].login").value("titular"))
            .andExpect(jsonPath("$.responsables[0].rol").value("HABITUAL"))
            .andExpect(jsonPath("$.responsables[0].operativo").value(true));
    }

    @Test
    void devuelveTodosLosTurnosSuperpuestos() throws Exception {
        Rotacion rotacion = persistirRotacion(true, "UTC");
        persistirTurno(rotacion, persistirUsuario("primero"), CONSULTA.minusSeconds(7200), CONSULTA.plusSeconds(600), false);
        persistirTurno(rotacion, persistirUsuario("segundo"), CONSULTA.minusSeconds(600), CONSULTA.plusSeconds(7200), false);

        consultar(rotacion, CONSULTA)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.responsables", hasSize(2)))
            .andExpect(jsonPath("$.responsables[0].operativo").value(true))
            .andExpect(jsonPath("$.responsables[1].operativo").value(true));
    }

    @Test
    void informaSinCobertura() throws Exception {
        Rotacion rotacion = persistirRotacion(true, "UTC");

        consultar(rotacion, CONSULTA)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("SIN_COBERTURA"))
            .andExpect(jsonPath("$.mensaje").value("Sin cobertura"))
            .andExpect(jsonPath("$.responsables", hasSize(0)));
    }

    @Test
    void priorizaReemplazoSinPerderTitular() throws Exception {
        Rotacion rotacion = persistirRotacion(true, "UTC");
        persistirTurno(rotacion, persistirUsuario("titular"), CONSULTA.minusSeconds(3600), CONSULTA.plusSeconds(3600), false);
        persistirTurno(rotacion, persistirUsuario("reemplazo"), CONSULTA.minusSeconds(300), CONSULTA.plusSeconds(300), true);

        consultar(rotacion, CONSULTA)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.responsables", hasSize(2)))
            .andExpect(jsonPath("$.responsables[0].login").value("reemplazo"))
            .andExpect(jsonPath("$.responsables[0].rol").value("REEMPLAZO"))
            .andExpect(jsonPath("$.responsables[0].operativo").value(true))
            .andExpect(jsonPath("$.responsables[1].login").value("titular"))
            .andExpect(jsonPath("$.responsables[1].operativo").value(false));
    }

    @Test
    void admiteVariosResponsablesSimultaneosSinInventarRoles() throws Exception {
        Rotacion rotacion = persistirRotacion(true, "UTC");
        persistirTurno(rotacion, persistirUsuario("responsable-a"), CONSULTA.minusSeconds(60), CONSULTA.plusSeconds(60), false);
        persistirTurno(rotacion, persistirUsuario("responsable-b"), CONSULTA.minusSeconds(60), CONSULTA.plusSeconds(60), false);

        consultar(rotacion, CONSULTA)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.responsables", hasSize(2)))
            .andExpect(jsonPath("$.responsables[0].rol", is("HABITUAL")))
            .andExpect(jsonPath("$.responsables[1].rol", is("HABITUAL")));
    }

    @Test
    void incluyeInicioYExcluyeFinDelTurno() throws Exception {
        Rotacion rotacion = persistirRotacion(true, "UTC");
        Instant desde = CONSULTA;
        Instant hasta = CONSULTA.plusSeconds(3600);
        persistirTurno(rotacion, persistirUsuario("limites"), desde, hasta, false);

        consultar(rotacion, desde).andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CON_COBERTURA"));
        consultar(rotacion, hasta).andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("SIN_COBERTURA"));
    }

    @Test
    void resuelveDuranteCambioDeHorarioDeVerano() throws Exception {
        ZoneId zona = ZoneId.of("America/New_York");
        Instant desde = ZonedDateTime.of(2026, 11, 1, 1, 0, 0, 0, zona).withEarlierOffsetAtOverlap().toInstant();
        Instant consultaRepetida = ZonedDateTime.of(2026, 11, 1, 1, 30, 0, 0, zona).withLaterOffsetAtOverlap().toInstant();
        Instant hasta = ZonedDateTime.of(2026, 11, 1, 2, 0, 0, 0, zona).toInstant();
        Rotacion rotacion = persistirRotacion(true, zona.getId());
        persistirTurno(rotacion, persistirUsuario("dst"), desde, hasta, false);

        consultar(rotacion, consultaRepetida)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("CON_COBERTURA"))
            .andExpect(jsonPath("$.zonaHoraria").value("America/New_York"));
    }

    @Test
    void respondeNotFoundParaRotacionInexistente() throws Exception {
        mockMvc
            .perform(get("/api/rotacions/{id}/guardia", Long.MAX_VALUE).queryParam("instante", CONSULTA.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void informaRotacionInactiva() throws Exception {
        Rotacion rotacion = persistirRotacion(false, "UTC");
        persistirTurno(rotacion, persistirUsuario("inactivo"), CONSULTA.minusSeconds(60), CONSULTA.plusSeconds(60), false);

        consultar(rotacion, CONSULTA)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("ROTACION_INACTIVA"))
            .andExpect(jsonPath("$.responsables", hasSize(0)));
    }

    private org.springframework.test.web.servlet.ResultActions consultar(Rotacion rotacion, Instant instante) throws Exception {
        return mockMvc.perform(get("/api/rotacions/{id}/guardia", rotacion.getId()).queryParam("instante", instante.toString()));
    }

    private Rotacion persistirRotacion(boolean activa, String zonaHoraria) {
        Equipo equipo = EquipoResourceIT.createEntity();
        em.persist(equipo);
        Rotacion rotacion = new Rotacion()
            .nombre("Rotación de prueba " + System.nanoTime())
            .tipo(TipoRotacion.PERSONALIZADA)
            .zonaHoraria(zonaHoraria)
            .activa(activa)
            .equipo(equipo);
        em.persist(rotacion);
        em.flush();
        return rotacion;
    }

    private User persistirUsuario(String login) {
        User user = UserResourceIT.createEntity();
        user.setLogin(login);
        em.persist(user);
        em.flush();
        return user;
    }

    private void persistirTurno(Rotacion rotacion, User responsable, Instant desde, Instant hasta, boolean reemplazo) {
        TurnoDeGuardia turno = new TurnoDeGuardia()
            .rotacion(rotacion)
            .responsable(responsable)
            .desde(desde)
            .hasta(hasta)
            .esReemplazo(reemplazo);
        em.persist(turno);
        em.flush();
    }
}
