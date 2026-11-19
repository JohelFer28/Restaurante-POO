package restaurante.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurante.datos.DatosDemo;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.OpcionEnUsoException;
import restaurante.excepciones.PedidoNoPermitidoException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// ids del menu demo: 1 Arroz 1500, 5 Pechuga 4500, 6 Filete 6500, 15 Brownie 2600, 17 Limonada 1500
class PedidosTest {
    private Restaurante restaurante;

    @BeforeEach
    void preparar() throws Exception {
        restaurante = DatosDemo.crearRestauranteDemo();
        restaurante.abrirVisita(1, new Mesero("Carlos"), List.of("Ana", "Luis"));
    }

    private Visita visita() throws Exception {
        return restaurante.obtenerMesa(1).getVisitaActual();
    }

    @Test
    void elPedidoGuardaElPrecioDelMomento() throws Exception {
        restaurante.agregarPedido(1, "Ana", 1, 2);   // 2 arroz a 1500
        OpcionMenu arroz = restaurante.getMenu().obtener(1);
        restaurante.getMenu().modificarOpcion(1, arroz.getNombre(), "x", 180, 9999);
        assertEquals(3000, visita().totalMesa());   // el cambio de precio no afecta lo ya pedido
    }

    @Test
    void pedidosSucesivosNoSobrescriben() throws Exception {
        restaurante.agregarPedido(1, "Ana", 1, 1);
        restaurante.agregarPedido(1, "Ana", 1, 1);
        assertEquals(2, visita().getPedidos().size());
        assertEquals(3000, visita().subtotalComensal("Ana"));
    }

    @Test
    void subtotalesPorPersonaYTotalDeLaMesa() throws Exception {
        restaurante.agregarPedido(1, "Ana", 5, 1);    // 4500
        restaurante.agregarPedido(1, "Ana", 1, 2);    // 3000
        restaurante.agregarPedido(1, "Luis", 17, 1);  // 1500
        assertEquals(7500, visita().subtotalComensal("Ana"));
        assertEquals(1500, visita().subtotalComensal("Luis"));
        assertEquals(9000, visita().totalMesa());
    }

    @Test
    void subtotalPorCategoriaYPorArticulo() throws Exception {
        restaurante.agregarPedido(1, "Ana", 1, 2);    // guarniciones 3000
        restaurante.agregarPedido(1, "Luis", 1, 1);   // guarniciones 1500
        restaurante.agregarPedido(1, "Luis", 17, 1);  // bebidas 1500
        assertEquals(4500, visita().subtotalPorCategoria().get("Guarniciones"));
        assertEquals(1500, visita().subtotalPorCategoria().get("Bebidas"));
        assertEquals(4500, visita().subtotalPorArticulo().get("Arroz blanco"));
    }

    @Test
    void cantidadInvalidaNoCreaPedido() throws Exception {
        assertThrows(DatoInvalidoException.class, () -> restaurante.agregarPedido(1, "Ana", 1, 0));
        assertThrows(DatoInvalidoException.class, () -> restaurante.agregarPedido(1, "Ana", 1, 100));
        assertTrue(visita().getPedidos().isEmpty());
    }

    @Test
    void opcionNoDisponibleNoSePuedePedir() throws Exception {
        restaurante.getMenu().desactivarOpcion(1);
        assertThrows(DatoInvalidoException.class, () -> restaurante.agregarPedido(1, "Ana", 1, 1));
        assertTrue(visita().getPedidos().isEmpty());
    }

    @Test
    void comensalInexistenteLanzaExcepcion() throws Exception {
        assertThrows(DatoInvalidoException.class, () -> restaurante.agregarPedido(1, "Pedro", 1, 1));
        assertTrue(visita().getPedidos().isEmpty());
    }

    @Test
    void elAliasSePuedeEscribirSinTildesNiMayusculas() throws Exception {
        restaurante.agregarPedido(1, "ANA", 1, 1);
        assertEquals(1500, visita().subtotalComensal("ana"));
    }

    @Test
    void noSePuedePedirEnMesaLibre() {
        assertThrows(PedidoNoPermitidoException.class, () -> restaurante.agregarPedido(2, "Ana", 1, 1));
    }

    @Test
    void noSePuedePedirDespuesDeSolicitarLaCuenta() throws Exception {
        restaurante.agregarPedido(1, "Ana", 1, 1);
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        assertThrows(PedidoNoPermitidoException.class, () -> restaurante.agregarPedido(1, "Ana", 1, 1));
        assertEquals(1, visita().getPedidos().size());
    }

    @Test
    void noSePuedePedirEnVisitaCerrada() throws Exception {
        restaurante.solicitarCuenta(1, ModalidadCuenta.CONJUNTA);
        restaurante.cerrarVisita(1);
        assertThrows(PedidoNoPermitidoException.class, () -> restaurante.agregarPedido(1, "Ana", 1, 1));
    }

    @Test
    void anularConservaElRegistroYRestaDelTotal() throws Exception {
        LineaPedido linea = restaurante.agregarPedido(1, "Ana", 5, 1);
        restaurante.agregarPedido(1, "Ana", 1, 1);
        restaurante.anularPedido(1, linea.getId(), "Error del mesero");

        assertEquals(2, visita().getPedidos().size());          // sigue en la bitacora
        assertEquals(1, visita().getPedidosActivos().size());
        assertTrue(linea.estaAnulada());
        assertEquals("Error del mesero", linea.getMotivoAnulacion());
        assertNotNull(linea.getFechaAnulacion());
        assertEquals(1500, visita().totalMesa());
    }

    @Test
    void anularDosVecesOSinMotivoLanzaExcepcion() throws Exception {
        LineaPedido linea = restaurante.agregarPedido(1, "Ana", 5, 1);
        assertThrows(DatoInvalidoException.class, () -> restaurante.anularPedido(1, linea.getId(), " "));
        restaurante.anularPedido(1, linea.getId(), "Motivo");
        assertThrows(DatoInvalidoException.class, () -> restaurante.anularPedido(1, linea.getId(), "Otra vez"));
        assertThrows(DatoInvalidoException.class, () -> restaurante.anularPedido(1, 999, "No existe"));
    }

    @Test
    void unaOpcionPedidaYaNoSePuedeEliminarDelMenu() throws Exception {
        restaurante.agregarPedido(1, "Ana", 1, 1);
        assertThrows(OpcionEnUsoException.class, () -> restaurante.getMenu().eliminarOpcion(1));
    }
}
