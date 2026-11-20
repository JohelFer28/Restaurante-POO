package restaurante.estadisticas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurante.datos.DatosDemo;
import restaurante.dominio.Mesero;
import restaurante.dominio.ModalidadCuenta;
import restaurante.dominio.PagoEfectivo;
import restaurante.dominio.PagoTarjeta;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.observador.BitacoraEventos;
import restaurante.observador.TipoEventoRestaurante;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticasObserverTest {
    private Restaurante restaurante;
    private BitacoraEventos bitacora;
    private ServicioEstadisticas estadisticas;

    @BeforeEach
    void preparar() {
        restaurante = DatosDemo.crearRestauranteDemo();
        bitacora = new BitacoraEventos();
        restaurante.agregarObservador(bitacora);
        estadisticas = new ServicioEstadisticas(restaurante);
    }

    @Test
    void observerRegistraCicloCompletoDeVisita() throws Exception {
        restaurante.abrirVisita(1, new Mesero("Carlos"), List.of("Ana"));
        restaurante.agregarPedido(1, "Ana", 5, 1); // 4500
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        restaurante.registrarPago(1, null, 4500, new PagoEfectivo());
        restaurante.cerrarVisita(1);

        assertEquals(5, bitacora.getEventos().size());
        assertEquals(TipoEventoRestaurante.VISITA_ABIERTA, bitacora.getEventos().get(0).getTipo());
        assertEquals(TipoEventoRestaurante.PEDIDO_REGISTRADO, bitacora.getEventos().get(1).getTipo());
        assertEquals(TipoEventoRestaurante.CUENTA_SOLICITADA, bitacora.getEventos().get(2).getTipo());
        assertEquals(TipoEventoRestaurante.PAGO_REGISTRADO, bitacora.getEventos().get(3).getTipo());
        assertEquals(TipoEventoRestaurante.VISITA_CERRADA, bitacora.getEventos().get(4).getTipo());
    }

    @Test
    void estadisticasCalculanVentasTopArticulosYVisitas() throws Exception {
        restaurante.abrirVisita(1, new Mesero("Carlos"), List.of("Ana", "Luis"));
        restaurante.agregarPedido(1, "Ana", 5, 2);   // 2 Pechugas = 9000
        restaurante.agregarPedido(1, "Luis", 17, 3); // 3 Limonadas = 4500
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        restaurante.registrarPago(1, null, 13500, new PagoTarjeta());
        restaurante.cerrarVisita(1);

        LocalDate hoy = LocalDate.now();
        ServicioEstadisticas.ReporteVentas ventas = estadisticas.ventasCobradas(hoy, hoy);
        assertEquals(13500, ventas.getTotalCobrado());
        assertEquals(1, ventas.getCantidadPagos());
        assertEquals(13500, ventas.getCobradoPorMetodo().get("Tarjeta"));

        List<ServicioEstadisticas.ItemMasSolicitado> top = estadisticas.articulosMasSolicitados(hoy, hoy, null);
        assertEquals(2, top.size());
        assertEquals("Limonada", top.get(0).getNombreOpcion());
        assertEquals(3, top.get(0).getUnidadesVendidas());

        ServicioEstadisticas.ReporteVisitas repVisitas = estadisticas.resumenVisitasCerradas(hoy, hoy);
        assertEquals(1, repVisitas.getVisitasCerradas());
        assertEquals(2, repVisitas.getTotalComensales());
        assertEquals(13500, repVisitas.getTicketPromedio());
    }

    @Test
    void rangoDeFechasInvertidoLanzaExcepcion() {
        LocalDate hoy = LocalDate.now();
        assertThrows(DatoInvalidoException.class,
                () -> estadisticas.ventasCobradas(hoy.plusDays(1), hoy));
    }

    @Test
    void rangoSinDatosDevuelveResultadoVacioComprensible() throws Exception {
        LocalDate pasado = LocalDate.of(2020, 1, 1);
        ServicioEstadisticas.ReporteVentas ventas = estadisticas.ventasCobradas(pasado, pasado);
        assertEquals(0, ventas.getTotalCobrado());
        assertTrue(ventas.getExplicacion().contains("No se registraron pagos"));
    }
}
