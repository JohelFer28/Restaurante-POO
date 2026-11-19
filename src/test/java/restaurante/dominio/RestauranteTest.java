package restaurante.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
import restaurante.excepciones.TransicionInvalidaException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestauranteTest {
    private Restaurante restaurante;
    private Mesero mesero;

    @BeforeEach
    void preparar() throws Exception {
        restaurante = new Restaurante(3, new Menu());
        mesero = new Mesero("Carlos");
    }

    @Test
    void abrirVisitaOcupaLaMesa() throws Exception {
        Visita v = restaurante.abrirVisita(1, mesero, List.of("Ana", "Luis"));
        assertFalse(restaurante.obtenerMesa(1).estaDisponible());
        assertTrue(v.estaAbierta());
        assertEquals(2, v.getComensales().size());
    }

    @Test
    void abrirEnMesaOcupadaLanzaExcepcionYConservaLaVisita() throws Exception {
        Visita original = restaurante.abrirVisita(1, mesero, List.of("Ana", "Luis"));
        assertThrows(MesaNoDisponibleException.class,
                () -> restaurante.abrirVisita(1, mesero, List.of("Pedro")));
        assertSame(original, restaurante.obtenerMesa(1).getVisitaActual());
        assertEquals(2, original.getComensales().size());
    }

    @Test
    void mesaInexistenteLanzaExcepcion() {
        assertThrows(MesaNoDisponibleException.class,
                () -> restaurante.abrirVisita(99, mesero, List.of("Ana")));
    }

    @Test
    void aliasRepetidoNoOcupaLaMesa() throws Exception {
        assertThrows(DatoInvalidoException.class,
                () -> restaurante.abrirVisita(1, mesero, List.of("Ana", "Luis", "Ana")));
        assertTrue(restaurante.obtenerMesa(1).estaDisponible());
        assertNull(restaurante.obtenerMesa(1).getVisitaActual());
    }

    @Test
    void aliasRepetidoIgnoraTildesYMayusculas() {
        assertThrows(DatoInvalidoException.class,
                () -> restaurante.abrirVisita(1, mesero, List.of("José", "jose")));
    }

    @Test
    void visitaSinComensalesLanzaExcepcion() throws Exception {
        assertThrows(DatoInvalidoException.class,
                () -> restaurante.abrirVisita(1, mesero, List.of()));
        assertTrue(restaurante.obtenerMesa(1).estaDisponible());
    }

    @Test
    void visitaSinMeseroLanzaExcepcion() throws Exception {
        assertThrows(DatoInvalidoException.class,
                () -> restaurante.abrirVisita(1, null, List.of("Ana")));
        assertTrue(restaurante.obtenerMesa(1).estaDisponible());
    }

    @Test
    void solicitarCuentaEnMesaLibreLanzaExcepcionYNoCambiaElEstado() throws Exception {
        assertThrows(TransicionInvalidaException.class, () -> restaurante.solicitarCuenta(1));
        assertTrue(restaurante.obtenerMesa(1).estaDisponible());
    }

    @Test
    void cerrarSinSolicitarCuentaLanzaExcepcion() throws Exception {
        restaurante.abrirVisita(1, mesero, List.of("Ana"));
        assertThrows(TransicionInvalidaException.class, () -> restaurante.cerrarVisita(1));
        assertEquals("Ocupada", restaurante.obtenerMesa(1).getNombreEstado());
        assertTrue(restaurante.getHistorial().isEmpty());
    }

    @Test
    void mesaPendientePagoSigueOcupada() throws Exception {
        restaurante.abrirVisita(1, mesero, List.of("Ana"));
        restaurante.solicitarCuenta(1);
        assertThrows(MesaNoDisponibleException.class,
                () -> restaurante.abrirVisita(1, mesero, List.of("Pedro")));
        assertEquals("Pendiente de pago", restaurante.obtenerMesa(1).getNombreEstado());
    }

    @Test
    void cicloCompletoDejaLaMesaLibreYGuardaElHistorial() throws Exception {
        restaurante.abrirVisita(1, mesero, List.of("Ana"));
        restaurante.solicitarCuenta(1);
        Visita cerrada = restaurante.cerrarVisita(1);

        assertTrue(restaurante.obtenerMesa(1).estaDisponible());
        assertFalse(cerrada.estaAbierta());
        assertNotNull(cerrada.getCierre());
        assertEquals(1, restaurante.getHistorial().size());

        // la mesa se puede volver a ocupar
        restaurante.abrirVisita(1, mesero, List.of("Luis"));
        assertFalse(restaurante.obtenerMesa(1).estaDisponible());
    }

    @Test
    void cantidadDeMesasInvalidaLanzaExcepcion() {
        assertThrows(DatoInvalidoException.class, () -> new Restaurante(0, new Menu()));
    }

    @Test
    void listaDeMesasEsUnaCopia() {
        List<Mesa> copia = restaurante.getMesas();
        assertThrows(UnsupportedOperationException.class, () -> copia.clear());
        assertEquals(3, restaurante.getMesas().size());
    }
}
