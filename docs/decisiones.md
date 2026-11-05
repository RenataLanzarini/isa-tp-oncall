# Decisiones de diseño

## Extensión 5: ventanas de mantenimiento

### Modelo e integración

La extensión se integró al modelo existente con tres entidades. `VentanaDeMantenimiento` define el período programado; `ServicioEnVentana` representa explícitamente la asociación entre un `Servicio` y una ventana; y `ExcepcionDeVentana` registra inclusiones o cancelaciones puntuales. Se eligió una entidad intermedia, en lugar de una relación muchos-a-muchos directa, para poder activar o desactivar la asociación y agregar una nota sin modificar las entidades base.

Las relaciones son muchos-a-uno desde `ServicioEnVentana` hacia `Servicio` y `VentanaDeMantenimiento`, y desde `ExcepcionDeVentana` hacia `VentanaDeMantenimiento`. Una ventana puede asociarse con varios servicios y tener varias excepciones. La combinación servicio-ventana no puede repetirse; esta condición se valida en la capa de servicio.

### Representación temporal

Se admiten dos tipos de ventana:

- `UNICA`: usa instantes absolutos `inicio` y `fin`. El fin debe ser posterior al inicio y no se aceptan campos de recurrencia.
- `SEMANAL`: usa `diaSemana`, `minutoInicio`, `minutoFin`, `vigenteDesde` y, opcionalmente, `vigenteHasta`. Los minutos se expresan desde el comienzo del día en la zona horaria indicada. Si `minutoFin` es menor o igual que `minutoInicio`, el final corresponde al día siguiente; la igualdad representa un intervalo de 24 horas.

La zona horaria se guarda como identificador IANA/Java `ZoneId` y se valida antes de persistir. Esto evita depender de la zona horaria del servidor y permite contemplar cambios de horario oficiales. Una vigencia con fecha final anterior a la inicial es rechazada.

### Excepciones y casos borde

`INCLUSION` representa una ocurrencia extraordinaria y `CANCELACION` permite excluir una ocurrencia programada. Ambas usan instantes absolutos `desde` y `hasta`, y el segundo debe ser estrictamente posterior al primero. En esta etapa las excepciones quedan persistidas y disponibles mediante CRUD; no existe todavía un evaluador temporal que las aplique automáticamente.

Las ventanas pueden marcarse inactivas y las asociaciones servicio-ventana también. Esta separación permite desactivar una ventana completa o solamente su aplicación a un servicio.

### Validación y verificación

Las restricciones simples se expresaron en el JDL y en Bean Validation. Las condiciones entre campos se implementaron en la capa de servicio para que se apliquen tanto al crear como al actualizar. Los errores se traducen a respuestas HTTP 400 con claves identificables.

Se verificaron los CRUD generados, las relaciones, las migraciones Liquibase y las validaciones mediante pruebas de integración. Los casos cubiertos incluyen ventanas únicas y semanales válidas, intervalos inválidos, campos incompatibles con el tipo, zona horaria inválida, vigencias incoherentes, excepciones inválidas y asociaciones duplicadas.

El proyecto base no contiene un proceso automático que transforme alertas en incidentes. Por esa razón, la extensión modela los períodos en los que una alerta no debería generar un incidente, pero no implementa una supresión ejecutable. No se presenta esa conducta como probada.

## Regla C: quién está de guardia

> Sección reservada para completar personalmente.

- Decisiones tomadas ante solapamientos y múltiples responsables:
- Respuesta elegida cuando no existe cobertura:
- Tratamiento de zonas horarias y horario de verano:
- Prioridad de reemplazos frente a la rotación regular:
- Servicio y endpoints implementados:
- Casos de prueba y forma de verificación:
