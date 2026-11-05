package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.service.GuardiaService;
import ar.edu.um.isa.oncall.service.dto.GuardiaDTO;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for resolving who is on call for a rotation.
 */
@RestController
@RequestMapping("/api/rotacions")
public class GuardiaResource {

    private static final Logger LOG = LoggerFactory.getLogger(GuardiaResource.class);

    private final GuardiaService guardiaService;

    public GuardiaResource(GuardiaService guardiaService) {
        this.guardiaService = guardiaService;
    }

    @GetMapping("/{rotacionId}/guardia")
    public ResponseEntity<GuardiaDTO> getGuardia(
        @PathVariable("rotacionId") Long rotacionId,
        @RequestParam("instante") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant instante
    ) {
        LOG.debug("REST request to resolve Guardia for rotation {} at {}", rotacionId, instante);
        return ResponseEntity.of(guardiaService.resolver(rotacionId, instante));
    }
}
