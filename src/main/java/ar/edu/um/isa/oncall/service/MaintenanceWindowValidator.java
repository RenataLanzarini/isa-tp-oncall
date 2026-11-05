package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.ExcepcionDeVentana;
import ar.edu.um.isa.oncall.domain.VentanaDeMantenimiento;
import ar.edu.um.isa.oncall.domain.enumeration.TipoVentana;
import java.time.DateTimeException;
import java.time.ZoneId;

final class MaintenanceWindowValidator {

    private MaintenanceWindowValidator() {}

    static void validate(VentanaDeMantenimiento ventana) {
        validateTimeZone(ventana);

        if (ventana.getTipo() == TipoVentana.UNICA) {
            validateOneTimeWindow(ventana);
        } else if (ventana.getTipo() == TipoVentana.SEMANAL) {
            validateWeeklyWindow(ventana);
        }

        if (
            ventana.getVigenteDesde() != null &&
            ventana.getVigenteHasta() != null &&
            ventana.getVigenteHasta().isBefore(ventana.getVigenteDesde())
        ) {
            throw invalidWindow("La vigencia hasta no puede ser anterior a la vigencia desde", "invalidValidity");
        }
    }

    static void validate(ExcepcionDeVentana excepcion) {
        if (excepcion.getDesde() != null && excepcion.getHasta() != null && !excepcion.getHasta().isAfter(excepcion.getDesde())) {
            throw new MaintenanceWindowValidationException(
                "El fin de la excepción debe ser posterior a su inicio",
                "excepcionDeVentana",
                "invalidInterval"
            );
        }
    }

    private static void validateTimeZone(VentanaDeMantenimiento ventana) {
        if (ventana.getZonaHoraria() == null) {
            return;
        }
        try {
            ZoneId.of(ventana.getZonaHoraria());
        } catch (DateTimeException exception) {
            throw invalidWindow("La zona horaria no es válida", "invalidTimeZone");
        }
    }

    private static void validateOneTimeWindow(VentanaDeMantenimiento ventana) {
        if (ventana.getInicio() == null || ventana.getFin() == null) {
            throw invalidWindow("Una ventana UNICA requiere inicio y fin", "missingOneTimeFields");
        }
        if (!ventana.getFin().isAfter(ventana.getInicio())) {
            throw invalidWindow("El fin de una ventana UNICA debe ser posterior al inicio", "invalidInterval");
        }
        if (
            ventana.getDiaSemana() != null ||
            ventana.getMinutoInicio() != null ||
            ventana.getMinutoFin() != null ||
            ventana.getVigenteDesde() != null ||
            ventana.getVigenteHasta() != null
        ) {
            throw invalidWindow("Una ventana UNICA no admite campos de recurrencia semanal", "unexpectedWeeklyFields");
        }
    }

    private static void validateWeeklyWindow(VentanaDeMantenimiento ventana) {
        if (
            ventana.getDiaSemana() == null ||
            ventana.getMinutoInicio() == null ||
            ventana.getMinutoFin() == null ||
            ventana.getVigenteDesde() == null
        ) {
            throw invalidWindow("Una ventana SEMANAL requiere día, minutos de inicio y fin, y vigencia desde", "missingWeeklyFields");
        }
        if (ventana.getInicio() != null || ventana.getFin() != null) {
            throw invalidWindow("Una ventana SEMANAL no admite inicio y fin absolutos", "unexpectedOneTimeFields");
        }
    }

    private static MaintenanceWindowValidationException invalidWindow(String message, String key) {
        return new MaintenanceWindowValidationException(message, "ventanaDeMantenimiento", key);
    }
}
