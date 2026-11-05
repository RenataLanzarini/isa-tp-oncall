package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.ServicioEnVentana;
import ar.edu.um.isa.oncall.domain.VentanaDeMantenimiento;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import ar.edu.um.isa.oncall.service.dto.ServicioEnVentanaDTO;
import ar.edu.um.isa.oncall.service.dto.VentanaDeMantenimientoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServicioEnVentana} and its DTO {@link ServicioEnVentanaDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServicioEnVentanaMapper extends EntityMapper<ServicioEnVentanaDTO, ServicioEnVentana> {
    @Mapping(target = "servicio", source = "servicio", qualifiedByName = "servicioNombre")
    @Mapping(target = "ventana", source = "ventana", qualifiedByName = "ventanaDeMantenimientoNombre")
    ServicioEnVentanaDTO toDto(ServicioEnVentana s);

    @Named("servicioNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ServicioDTO toDtoServicioNombre(Servicio servicio);

    @Named("ventanaDeMantenimientoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    VentanaDeMantenimientoDTO toDtoVentanaDeMantenimientoNombre(VentanaDeMantenimiento ventanaDeMantenimiento);
}
