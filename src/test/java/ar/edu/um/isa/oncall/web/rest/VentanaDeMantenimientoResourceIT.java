package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.VentanaDeMantenimientoAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.VentanaDeMantenimiento;
import ar.edu.um.isa.oncall.domain.enumeration.DiaSemana;
import ar.edu.um.isa.oncall.domain.enumeration.TipoVentana;
import ar.edu.um.isa.oncall.repository.VentanaDeMantenimientoRepository;
import ar.edu.um.isa.oncall.service.dto.VentanaDeMantenimientoDTO;
import ar.edu.um.isa.oncall.service.mapper.VentanaDeMantenimientoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VentanaDeMantenimientoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VentanaDeMantenimientoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final TipoVentana DEFAULT_TIPO = TipoVentana.UNICA;
    private static final TipoVentana UPDATED_TIPO = TipoVentana.SEMANAL;

    private static final String DEFAULT_ZONA_HORARIA = "UTC";
    private static final String UPDATED_ZONA_HORARIA = "America/Argentina/Buenos_Aires";

    private static final Instant DEFAULT_INICIO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INICIO = Instant.ofEpochMilli(1701729143509L);

    private static final Instant DEFAULT_FIN = Instant.ofEpochMilli(3600000L);
    private static final Instant UPDATED_FIN = Instant.ofEpochMilli(1701732743509L);

    private static final DiaSemana DEFAULT_DIA_SEMANA = DiaSemana.LUNES;
    private static final DiaSemana UPDATED_DIA_SEMANA = DiaSemana.MARTES;

    private static final Integer DEFAULT_MINUTO_INICIO = 0;
    private static final Integer UPDATED_MINUTO_INICIO = 1;

    private static final Integer DEFAULT_MINUTO_FIN = 0;
    private static final Integer UPDATED_MINUTO_FIN = 1;

    private static final LocalDate DEFAULT_VIGENTE_DESDE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VIGENTE_DESDE = LocalDate.parse("2023-12-04");

    private static final LocalDate DEFAULT_VIGENTE_HASTA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VIGENTE_HASTA = LocalDate.parse("2023-12-04");

    private static final Boolean DEFAULT_ACTIVA = false;
    private static final Boolean UPDATED_ACTIVA = true;

    private static final String ENTITY_API_URL = "/api/ventana-de-mantenimientos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VentanaDeMantenimientoRepository ventanaDeMantenimientoRepository;

    @Autowired
    private VentanaDeMantenimientoMapper ventanaDeMantenimientoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVentanaDeMantenimientoMockMvc;

    private VentanaDeMantenimiento ventanaDeMantenimiento;

    private VentanaDeMantenimiento insertedVentanaDeMantenimiento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VentanaDeMantenimiento createEntity() {
        return new VentanaDeMantenimiento()
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .tipo(DEFAULT_TIPO)
            .zonaHoraria(DEFAULT_ZONA_HORARIA)
            .inicio(DEFAULT_INICIO)
            .fin(DEFAULT_FIN)
            .activa(DEFAULT_ACTIVA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VentanaDeMantenimiento createUpdatedEntity() {
        return new VentanaDeMantenimiento()
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .tipo(UPDATED_TIPO)
            .zonaHoraria(UPDATED_ZONA_HORARIA)
            .diaSemana(UPDATED_DIA_SEMANA)
            .minutoInicio(UPDATED_MINUTO_INICIO)
            .minutoFin(UPDATED_MINUTO_FIN)
            .vigenteDesde(UPDATED_VIGENTE_DESDE)
            .vigenteHasta(UPDATED_VIGENTE_HASTA)
            .activa(UPDATED_ACTIVA);
    }

    @BeforeEach
    void initTest() {
        ventanaDeMantenimiento = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVentanaDeMantenimiento != null) {
            ventanaDeMantenimientoRepository.delete(insertedVentanaDeMantenimiento);
            insertedVentanaDeMantenimiento = null;
        }
    }

    @Test
    @Transactional
    void createVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);
        var returnedVentanaDeMantenimientoDTO = om.readValue(
            restVentanaDeMantenimientoMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VentanaDeMantenimientoDTO.class
        );

        // Validate the VentanaDeMantenimiento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVentanaDeMantenimiento = ventanaDeMantenimientoMapper.toEntity(returnedVentanaDeMantenimientoDTO);
        assertVentanaDeMantenimientoUpdatableFieldsEquals(
            returnedVentanaDeMantenimiento,
            getPersistedVentanaDeMantenimiento(returnedVentanaDeMantenimiento)
        );

        insertedVentanaDeMantenimiento = returnedVentanaDeMantenimiento;
    }

    @Test
    @Transactional
    void createWeeklyVentanaDeMantenimiento() throws Exception {
        ventanaDeMantenimiento = createUpdatedEntity();
        long databaseSizeBeforeCreate = getRepositoryCount();

        VentanaDeMantenimientoDTO dto = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);
        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isCreated());

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void rejectOneTimeWindowWithoutAbsoluteInterval() throws Exception {
        ventanaDeMantenimiento.setInicio(null);
        assertInvalidWindow("error.missingOneTimeFields");
    }

    @Test
    @Transactional
    void rejectOneTimeWindowWithInvalidInterval() throws Exception {
        ventanaDeMantenimiento.setFin(DEFAULT_INICIO);
        assertInvalidWindow("error.invalidInterval");
    }

    @Test
    @Transactional
    void rejectOneTimeWindowWithWeeklyFields() throws Exception {
        ventanaDeMantenimiento.setDiaSemana(DEFAULT_DIA_SEMANA);
        assertInvalidWindow("error.unexpectedWeeklyFields");
    }

    @Test
    @Transactional
    void rejectWeeklyWindowWithoutRecurrenceFields() throws Exception {
        ventanaDeMantenimiento = createUpdatedEntity();
        ventanaDeMantenimiento.setMinutoFin(null);
        assertInvalidWindow("error.missingWeeklyFields");
    }

    @Test
    @Transactional
    void rejectWindowWithInvalidValidity() throws Exception {
        ventanaDeMantenimiento = createUpdatedEntity();
        ventanaDeMantenimiento.setVigenteHasta(UPDATED_VIGENTE_DESDE.minusDays(1));
        assertInvalidWindow("error.invalidValidity");
    }

    @Test
    @Transactional
    void rejectWindowWithInvalidTimeZone() throws Exception {
        ventanaDeMantenimiento.setZonaHoraria("zona-inexistente");
        assertInvalidWindow("error.invalidTimeZone");
    }

    @Test
    @Transactional
    void createVentanaDeMantenimientoWithExistingId() throws Exception {
        // Create the VentanaDeMantenimiento with an existing ID
        ventanaDeMantenimiento.setId(1L);
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventanaDeMantenimiento.setNombre(null);

        // Create the VentanaDeMantenimiento, which fails.
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventanaDeMantenimiento.setTipo(null);

        // Create the VentanaDeMantenimiento, which fails.
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkZonaHorariaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventanaDeMantenimiento.setZonaHoraria(null);

        // Create the VentanaDeMantenimiento, which fails.
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActivaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ventanaDeMantenimiento.setActiva(null);

        // Create the VentanaDeMantenimiento, which fails.
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVentanaDeMantenimientos() throws Exception {
        // Initialize the database
        insertedVentanaDeMantenimiento = ventanaDeMantenimientoRepository.saveAndFlush(ventanaDeMantenimiento);

        // Get all the ventanaDeMantenimientoList
        restVentanaDeMantenimientoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ventanaDeMantenimiento.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].zonaHoraria").value(hasItem(DEFAULT_ZONA_HORARIA)))
            .andExpect(jsonPath("$.[*].inicio").value(hasItem(DEFAULT_INICIO.toString())))
            .andExpect(jsonPath("$.[*].fin").value(hasItem(DEFAULT_FIN.toString())))
            .andExpect(jsonPath("$.[*].activa").value(hasItem(DEFAULT_ACTIVA)));
    }

    @Test
    @Transactional
    void getVentanaDeMantenimiento() throws Exception {
        // Initialize the database
        insertedVentanaDeMantenimiento = ventanaDeMantenimientoRepository.saveAndFlush(ventanaDeMantenimiento);

        // Get the ventanaDeMantenimiento
        restVentanaDeMantenimientoMockMvc
            .perform(get(ENTITY_API_URL_ID, ventanaDeMantenimiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ventanaDeMantenimiento.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.zonaHoraria").value(DEFAULT_ZONA_HORARIA))
            .andExpect(jsonPath("$.inicio").value(DEFAULT_INICIO.toString()))
            .andExpect(jsonPath("$.fin").value(DEFAULT_FIN.toString()))
            .andExpect(jsonPath("$.activa").value(DEFAULT_ACTIVA));
    }

    @Test
    @Transactional
    void getNonExistingVentanaDeMantenimiento() throws Exception {
        // Get the ventanaDeMantenimiento
        restVentanaDeMantenimientoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVentanaDeMantenimiento() throws Exception {
        // Initialize the database
        insertedVentanaDeMantenimiento = ventanaDeMantenimientoRepository.saveAndFlush(ventanaDeMantenimiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ventanaDeMantenimiento
        VentanaDeMantenimiento updatedVentanaDeMantenimiento = ventanaDeMantenimientoRepository
            .findById(ventanaDeMantenimiento.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedVentanaDeMantenimiento are not directly saved in db
        em.detach(updatedVentanaDeMantenimiento);
        updatedVentanaDeMantenimiento
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .tipo(UPDATED_TIPO)
            .zonaHoraria(UPDATED_ZONA_HORARIA)
            .inicio(null)
            .fin(null)
            .diaSemana(UPDATED_DIA_SEMANA)
            .minutoInicio(UPDATED_MINUTO_INICIO)
            .minutoFin(UPDATED_MINUTO_FIN)
            .vigenteDesde(UPDATED_VIGENTE_DESDE)
            .vigenteHasta(UPDATED_VIGENTE_HASTA)
            .activa(UPDATED_ACTIVA);
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(updatedVentanaDeMantenimiento);

        restVentanaDeMantenimientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventanaDeMantenimientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
            )
            .andExpect(status().isOk());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVentanaDeMantenimientoToMatchAllProperties(updatedVentanaDeMantenimiento);
    }

    @Test
    @Transactional
    void putNonExistingVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventanaDeMantenimiento.setId(longCount.incrementAndGet());

        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentanaDeMantenimientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventanaDeMantenimientoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventanaDeMantenimiento.setId(longCount.incrementAndGet());

        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentanaDeMantenimientoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventanaDeMantenimiento.setId(longCount.incrementAndGet());

        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentanaDeMantenimientoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ventanaDeMantenimientoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVentanaDeMantenimientoWithPatch() throws Exception {
        // Initialize the database
        insertedVentanaDeMantenimiento = ventanaDeMantenimientoRepository.saveAndFlush(ventanaDeMantenimiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ventanaDeMantenimiento using partial update
        VentanaDeMantenimiento partialUpdatedVentanaDeMantenimiento = new VentanaDeMantenimiento();
        partialUpdatedVentanaDeMantenimiento.setId(ventanaDeMantenimiento.getId());

        partialUpdatedVentanaDeMantenimiento
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .zonaHoraria(UPDATED_ZONA_HORARIA)
            .activa(UPDATED_ACTIVA);

        restVentanaDeMantenimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVentanaDeMantenimiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVentanaDeMantenimiento))
            )
            .andExpect(status().isOk());

        // Validate the VentanaDeMantenimiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentanaDeMantenimientoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVentanaDeMantenimiento, ventanaDeMantenimiento),
            getPersistedVentanaDeMantenimiento(ventanaDeMantenimiento)
        );
    }

    @Test
    @Transactional
    void fullUpdateVentanaDeMantenimientoWithPatch() throws Exception {
        // Initialize the database
        insertedVentanaDeMantenimiento = ventanaDeMantenimientoRepository.saveAndFlush(ventanaDeMantenimiento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ventanaDeMantenimiento using partial update
        VentanaDeMantenimiento partialUpdatedVentanaDeMantenimiento = new VentanaDeMantenimiento();
        partialUpdatedVentanaDeMantenimiento.setId(ventanaDeMantenimiento.getId());

        partialUpdatedVentanaDeMantenimiento
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .tipo(DEFAULT_TIPO)
            .zonaHoraria(UPDATED_ZONA_HORARIA)
            .inicio(UPDATED_INICIO)
            .fin(UPDATED_FIN)
            .activa(UPDATED_ACTIVA);

        restVentanaDeMantenimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVentanaDeMantenimiento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVentanaDeMantenimiento))
            )
            .andExpect(status().isOk());

        // Validate the VentanaDeMantenimiento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVentanaDeMantenimientoUpdatableFieldsEquals(
            partialUpdatedVentanaDeMantenimiento,
            getPersistedVentanaDeMantenimiento(partialUpdatedVentanaDeMantenimiento)
        );
    }

    @Test
    @Transactional
    void patchNonExistingVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventanaDeMantenimiento.setId(longCount.incrementAndGet());

        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentanaDeMantenimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ventanaDeMantenimientoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventanaDeMantenimiento.setId(longCount.incrementAndGet());

        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentanaDeMantenimientoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVentanaDeMantenimiento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ventanaDeMantenimiento.setId(longCount.incrementAndGet());

        // Create the VentanaDeMantenimiento
        VentanaDeMantenimientoDTO ventanaDeMantenimientoDTO = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentanaDeMantenimientoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ventanaDeMantenimientoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VentanaDeMantenimiento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVentanaDeMantenimiento() throws Exception {
        // Initialize the database
        insertedVentanaDeMantenimiento = ventanaDeMantenimientoRepository.saveAndFlush(ventanaDeMantenimiento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ventanaDeMantenimiento
        restVentanaDeMantenimientoMockMvc
            .perform(delete(ENTITY_API_URL_ID, ventanaDeMantenimiento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ventanaDeMantenimientoRepository.count();
    }

    private void assertInvalidWindow(String message) throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        VentanaDeMantenimientoDTO dto = ventanaDeMantenimientoMapper.toDto(ventanaDeMantenimiento);

        restVentanaDeMantenimientoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(message));

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected VentanaDeMantenimiento getPersistedVentanaDeMantenimiento(VentanaDeMantenimiento ventanaDeMantenimiento) {
        return ventanaDeMantenimientoRepository.findById(ventanaDeMantenimiento.getId()).orElseThrow();
    }

    protected void assertPersistedVentanaDeMantenimientoToMatchAllProperties(VentanaDeMantenimiento expectedVentanaDeMantenimiento) {
        assertVentanaDeMantenimientoAllPropertiesEquals(
            expectedVentanaDeMantenimiento,
            getPersistedVentanaDeMantenimiento(expectedVentanaDeMantenimiento)
        );
    }

    protected void assertPersistedVentanaDeMantenimientoToMatchUpdatableProperties(VentanaDeMantenimiento expectedVentanaDeMantenimiento) {
        assertVentanaDeMantenimientoAllUpdatablePropertiesEquals(
            expectedVentanaDeMantenimiento,
            getPersistedVentanaDeMantenimiento(expectedVentanaDeMantenimiento)
        );
    }
}
