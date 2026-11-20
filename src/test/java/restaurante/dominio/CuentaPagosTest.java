package restaurante.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurante.datos.DatosDemo;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.PagoInvalidoException;
import restaurante.excepciones.SaldoPendienteException;
import restaurante.excepciones.TransicionInvalidaException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Ana debe 7500 (pechuga 4500 + 2 arroz 3000) y Luis debe 1500 (limonada), total 9000
class CuentaPagosTest {
    private Restaurante restaurante;

    @BeforeEach
    void preparar() throws Exception {
        restaurante = DatosDemo.crearRestauranteDemo();
        restaurante.abrirVisita(1, new Mesero("Carlos"), List.of("Ana", "Luis"));
        restaurante.agregarPedido(1, "Ana", 5, 1);
        restaurante.agregarPedido(1, "Ana", 1, 2);
        restaurante.agregarPedido(1, "Luis", 17, 1);
    }

    private Visita visita() throws Exception {
        return restaurante.obtenerMesa(1).getVisitaActual();
    }

    @Test
    void cuentaConjuntaConPagosParciales() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        restaurante.registrarPago(1, null, 4000, new PagoEfectivo());
        assertEquals(5000, visita().getSaldoPendiente());
        restaurante.registrarPago(1, null, 5000, new PagoTarjeta());
        assertEquals(0, visita().getSaldoPendiente());
        restaurante.cerrarVisita(1);
        assertTrue(restaurante.obtenerMesa(1).estaDisponible());
    }

    @Test
    void pagoMayorAlSaldoLanzaExcepcionYNoCambiaNada() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        assertThrows(PagoInvalidoException.class,
                () -> restaurante.registrarPago(1, null, 9001, new PagoEfectivo()));
        assertEquals(9000, visita().getSaldoPendiente());
        assertTrue(visita().getPagos().isEmpty());
    }

    @Test
    void montoCeroONegativoLanzaExcepcion() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        assertThrows(PagoInvalidoException.class, () -> restaurante.registrarPago(1, null, 0, new PagoEfectivo()));
        assertThrows(PagoInvalidoException.class, () -> restaurante.registrarPago(1, null, -500, new PagoEfectivo()));
    }

    @Test
    void sinMetodoDePagoLanzaExcepcion() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        assertThrows(PagoInvalidoException.class, () -> restaurante.registrarPago(1, null, 1000, null));
    }

    @Test
    void noSeCierraConSaldoPendiente() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        restaurante.registrarPago(1, null, 8999, new PagoEfectivo());
        assertThrows(SaldoPendienteException.class, () -> restaurante.cerrarVisita(1));
        assertEquals("Pendiente de pago", restaurante.obtenerMesa(1).getNombreEstado());
        assertTrue(restaurante.getHistorial().isEmpty());
    }

    @Test
    void pagosIndividualesCadaUnoPagaLoSuyo() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.INDIVIDUAL);
        assertEquals(7500, visita().saldoComensal("Ana"));
        assertEquals(1500, visita().saldoComensal("Luis"));

        restaurante.registrarPago(1, "Ana", 7500, new PagoSinpe());
        assertEquals(0, visita().saldoComensal("Ana"));
        assertEquals(1500, visita().getSaldoPendiente());

        restaurante.registrarPago(1, "Luis", 1500, new PagoEfectivo());
        restaurante.cerrarVisita(1);
        assertEquals(1, restaurante.getHistorial().size());
    }

    @Test
    void enPagosIndividualesNoSePuedePagarMasDeLoPropio() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.INDIVIDUAL);
        // 2000 es menor al total de la mesa, pero Luis solo debe 1500
        assertThrows(PagoInvalidoException.class,
                () -> restaurante.registrarPago(1, "Luis", 2000, new PagoEfectivo()));
        assertEquals(1500, visita().saldoComensal("Luis"));
    }

    @Test
    void enPagosIndividualesElComensalEsObligatorio() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.INDIVIDUAL);
        assertThrows(PagoInvalidoException.class, () -> restaurante.registrarPago(1, null, 1000, new PagoEfectivo()));
        assertThrows(PagoInvalidoException.class, () -> restaurante.registrarPago(1, "Pedro", 1000, new PagoEfectivo()));
    }

    @Test
    void enCuentaConjuntaElPagoNoSeAsignaAUnaPersona() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        assertThrows(PagoInvalidoException.class, () -> restaurante.registrarPago(1, "Ana", 1000, new PagoEfectivo()));
    }

    @Test
    void noSePuedePagarAntesDeSolicitarLaCuenta() {
        assertThrows(TransicionInvalidaException.class,
                () -> restaurante.registrarPago(1, null, 1000, new PagoEfectivo()));
    }

    @Test
    void solicitarCuentaSinModalidadLanzaExcepcionYLaMesaSigueOcupada() throws Exception {
        assertThrows(DatoInvalidoException.class, () -> restaurante.solicitarCuenta(1, null));
        assertEquals("Ocupada", restaurante.obtenerMesa(1).getNombreEstado());
        restaurante.agregarPedido(1, "Ana", 1, 1);   // todavia se puede pedir
    }

    @Test
    void elPagoGuardaMetodoReferenciaYFecha() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        Pago p = restaurante.registrarPago(1, null, 1000, new PagoTarjeta());
        assertEquals("Tarjeta", p.getMetodo().getNombre());
        assertEquals("TRJ-M1-0001", p.getReferencia());
        assertNotNull(p.getFecha());
        assertEquals(1000, p.getMonto());
    }

    @Test
    void elHistorialConservaPedidosYPagos() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        restaurante.registrarPago(1, null, 9000, new PagoEfectivo());
        Visita cerrada = restaurante.cerrarVisita(1);

        assertEquals(3, cerrada.getPedidos().size());
        assertEquals(1, cerrada.getPagos().size());
        assertEquals(9000, cerrada.totalPagado());
        assertNotNull(cerrada.getCierre());
        assertSame(cerrada, restaurante.getHistorial().get(0));
    }

    @Test
    void conPedidoAnuladoElTotalAPagarSeReduce() throws Exception {
        restaurante.anularPedido(1, 1, "Error");   // anula la pechuga (4500)
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        assertEquals(4500, visita().getSaldoPendiente());
    }
}
