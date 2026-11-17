package restaurante.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.OpcionEnUsoException;
import restaurante.excepciones.OpcionNoEncontradaException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {
    private Menu menu;
    private Categoria postres;

    @BeforeEach
    void preparar() throws Exception {
        menu = new Menu();
        postres = menu.buscarCategoria("Postres");
    }

    @Test
    void agregarAsignaIdsConsecutivos() throws Exception {
        OpcionMenu a = menu.agregarOpcion("Flan", postres, "Flan casero", 260, 2200);
        OpcionMenu b = menu.agregarOpcion("Gelatina", postres, "De frutas", 80, 1200);
        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
    }

    @Test
    void precioNegativoLanzaExcepcionYNoAgrega() {
        assertThrows(DatoInvalidoException.class, () -> menu.agregarOpcion("Flan", postres, "x", 100, -1));
        assertTrue(menu.listarTodas().isEmpty());
    }

    @Test
    void caloriasNegativasLanzanExcepcion() {
        assertThrows(DatoInvalidoException.class, () -> menu.agregarOpcion("Flan", postres, "x", -1, 100));
    }

    @Test
    void modificarInvalidoDejaLaOpcionIntacta() throws Exception {
        OpcionMenu flan = menu.agregarOpcion("Flan", postres, "Flan casero", 260, 2200);
        assertThrows(DatoInvalidoException.class, () -> menu.modificarOpcion(flan.getId(), "Nuevo", "x", 100, -50));
        assertEquals("Flan", flan.getNombre());   // el nombre tampoco cambio
        assertEquals(2200, flan.getPrecio());
    }

    @Test
    void noSePuedeEliminarOpcionYaPedida() throws Exception {
        OpcionMenu flan = menu.agregarOpcion("Flan", postres, "x", 260, 2200);
        flan.marcarComoUsada();
        assertThrows(OpcionEnUsoException.class, () -> menu.eliminarOpcion(flan.getId()));
        assertEquals(1, menu.listarTodas().size());
    }

    @Test
    void eliminarOpcionNoUsadaFunciona() throws Exception {
        OpcionMenu flan = menu.agregarOpcion("Flan", postres, "x", 260, 2200);
        menu.eliminarOpcion(flan.getId());
        assertThrows(OpcionNoEncontradaException.class, () -> menu.obtener(flan.getId()));
    }

    @Test
    void buscarPorNombreIgnoraTildesYMayusculas() throws Exception {
        menu.agregarOpcion("Puré de papa", menu.buscarCategoria("Guarniciones"), "x", 220, 1800);
        assertEquals(1, menu.buscarPorNombre("PURE").size());
    }

    @Test
    void buscarPorRangoCaloriasIncluyeLosExtremos() throws Exception {
        menu.agregarOpcion("A", postres, "x", 100, 1000);
        menu.agregarOpcion("B", postres, "x", 200, 1000);
        menu.agregarOpcion("C", postres, "x", 300, 1000);
        assertEquals(2, menu.buscarPorRangoCalorias(100, 200).size());
    }

    @Test
    void rangoInvertidoLanzaExcepcion() {
        assertThrows(DatoInvalidoException.class, () -> menu.buscarPorRangoPrecio(500, 100));
    }

    @Test
    void listaDevueltaNoModificaElMenu() throws Exception {
        menu.agregarOpcion("Flan", postres, "x", 260, 2200);
        List<OpcionMenu> copia = menu.listarTodas();
        assertThrows(UnsupportedOperationException.class, () -> copia.clear());
        assertEquals(1, menu.listarTodas().size());
    }

    @Test
    void opcionesDesactivadasNoSalenEnElListadoDeDisponibles() throws Exception {
        OpcionMenu flan = menu.agregarOpcion("Flan", postres, "x", 260, 2200);
        menu.desactivarOpcion(flan.getId());
        assertTrue(menu.opcionesDisponibles(postres).isEmpty());
        assertEquals(1, menu.buscarPorCategoria(postres).size()); // la busqueda por categoria si la encuentra
    }

    @Test
    void noSePermiteCategoriaDuplicada() {
        assertThrows(DatoInvalidoException.class, () -> menu.agregarCategoria("proteinas"));
    }
}
