package ar.edu.um.isa.oncall.service.dto;

import java.time.Instant;

/**
 * A person matched by the on-call resolution, including traceability of the concrete shift.
 */
public record ResponsableGuardiaDTO(
    Long turnoId,
    Long usuarioId,
    String login,
    RolGuardia rol,
    boolean operativo,
    Instant desde,
    Instant hasta
) {
    public enum RolGuardia {
        HABITUAL,
        REEMPLAZO,
    }
}
