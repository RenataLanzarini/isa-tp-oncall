package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Read-only queries used to resolve who is on call at a given instant.
 */
public interface GuardiaRepository extends Repository<TurnoDeGuardia, Long> {
    @Query(
        """
        select turno
        from TurnoDeGuardia turno
        join fetch turno.responsable
        where turno.rotacion.id = :rotacionId
          and turno.desde <= :instante
          and turno.hasta > :instante
        """
    )
    List<TurnoDeGuardia> findTurnosActivos(@Param("rotacionId") Long rotacionId, @Param("instante") Instant instante);
}
