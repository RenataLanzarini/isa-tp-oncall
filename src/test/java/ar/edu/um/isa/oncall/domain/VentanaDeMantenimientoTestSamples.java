package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VentanaDeMantenimientoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static VentanaDeMantenimiento getVentanaDeMantenimientoSample1() {
        return new VentanaDeMantenimiento()
            .id(1L)
            .nombre("nombre1")
            .descripcion("descripcion1")
            .zonaHoraria("zonaHoraria1")
            .minutoInicio(1)
            .minutoFin(1);
    }

    public static VentanaDeMantenimiento getVentanaDeMantenimientoSample2() {
        return new VentanaDeMantenimiento()
            .id(2L)
            .nombre("nombre2")
            .descripcion("descripcion2")
            .zonaHoraria("zonaHoraria2")
            .minutoInicio(2)
            .minutoFin(2);
    }

    public static VentanaDeMantenimiento getVentanaDeMantenimientoRandomSampleGenerator() {
        return new VentanaDeMantenimiento()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .descripcion(UUID.randomUUID().toString())
            .zonaHoraria(UUID.randomUUID().toString())
            .minutoInicio(intCount.incrementAndGet())
            .minutoFin(intCount.incrementAndGet());
    }
}
