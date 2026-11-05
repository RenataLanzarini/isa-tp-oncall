package ar.edu.um.isa.oncall.service.dto;

import java.time.Instant;
import java.util.List;

/**
 * Result of resolving the people on call for one rotation at one instant.
 */
public record GuardiaDTO(
    Long rotacionId,
    String rotacionNombre,
    Instant instante,
    String zonaHoraria,
    EstadoCobertura estado,
    String mensaje,
    List<ResponsableGuardiaDTO> responsables
) {
    public enum EstadoCobertura {
        CON_COBERTURA,
        SIN_COBERTURA,
        ROTACION_INACTIVA,
    }
}
