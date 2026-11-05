package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.Rotacion;
import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import ar.edu.um.isa.oncall.repository.GuardiaRepository;
import ar.edu.um.isa.oncall.repository.RotacionRepository;
import ar.edu.um.isa.oncall.service.dto.GuardiaDTO;
import ar.edu.um.isa.oncall.service.dto.GuardiaDTO.EstadoCobertura;
import ar.edu.um.isa.oncall.service.dto.ResponsableGuardiaDTO;
import ar.edu.um.isa.oncall.service.dto.ResponsableGuardiaDTO.RolGuardia;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves the concrete shifts that cover a rotation at an instant.
 */
@Service
@Transactional(readOnly = true)
public class GuardiaService {

    private final RotacionRepository rotacionRepository;

    private final GuardiaRepository guardiaRepository;

    public GuardiaService(RotacionRepository rotacionRepository, GuardiaRepository guardiaRepository) {
        this.rotacionRepository = rotacionRepository;
        this.guardiaRepository = guardiaRepository;
    }

    public Optional<GuardiaDTO> resolver(Long rotacionId, Instant instante) {
        return rotacionRepository.findById(rotacionId).map(rotacion -> resolver(rotacion, instante));
    }

    private GuardiaDTO resolver(Rotacion rotacion, Instant instante) {
        String zonaHoraria = rotacion.getZonaHoraria();

        if (!Boolean.TRUE.equals(rotacion.getActiva())) {
            return resultado(rotacion, instante, zonaHoraria, EstadoCobertura.ROTACION_INACTIVA, "Rotación inactiva", List.of());
        }

        List<TurnoDeGuardia> turnos = guardiaRepository.findTurnosActivos(rotacion.getId(), instante);
        if (turnos.isEmpty()) {
            return resultado(rotacion, instante, zonaHoraria, EstadoCobertura.SIN_COBERTURA, "Sin cobertura", List.of());
        }

        boolean hayReemplazo = turnos.stream().anyMatch(turno -> Boolean.TRUE.equals(turno.getEsReemplazo()));
        List<ResponsableGuardiaDTO> responsables = turnos
            .stream()
            .sorted(
                Comparator.comparing((TurnoDeGuardia turno) -> !Boolean.TRUE.equals(turno.getEsReemplazo()))
                    .thenComparing(TurnoDeGuardia::getDesde)
                    .thenComparing(TurnoDeGuardia::getId)
            )
            .map(turno -> toDto(turno, hayReemplazo))
            .toList();

        return resultado(rotacion, instante, zonaHoraria, EstadoCobertura.CON_COBERTURA, "Con cobertura", responsables);
    }

    private ResponsableGuardiaDTO toDto(TurnoDeGuardia turno, boolean hayReemplazo) {
        boolean esReemplazo = Boolean.TRUE.equals(turno.getEsReemplazo());
        return new ResponsableGuardiaDTO(
            turno.getId(),
            turno.getResponsable().getId(),
            turno.getResponsable().getLogin(),
            esReemplazo ? RolGuardia.REEMPLAZO : RolGuardia.HABITUAL,
            !hayReemplazo || esReemplazo,
            turno.getDesde(),
            turno.getHasta()
        );
    }

    private GuardiaDTO resultado(
        Rotacion rotacion,
        Instant instante,
        String zonaHoraria,
        EstadoCobertura estado,
        String mensaje,
        List<ResponsableGuardiaDTO> responsables
    ) {
        return new GuardiaDTO(rotacion.getId(), rotacion.getNombre(), instante, zonaHoraria, estado, mensaje, responsables);
    }
}
