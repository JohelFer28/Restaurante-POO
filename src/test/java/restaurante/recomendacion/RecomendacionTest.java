package restaurante.recomendacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import restaurante.datos.DatosDemo;
import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;
import restaurante.excepciones.DatoInvalidoException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecomendacionTest {
    private Menu demo;
    private ServicioRecomendacion poda;
    private ServicioRecomendacion fuerzaBruta;

    @BeforeEach
    void preparar() {
        demo = DatosDemo.crearMenuDemo();
        poda = new ServicioRecomendacion(new RecomendacionPorPoda());
        fuerzaBruta = new ServicioRecomendacion(new RecomendacionFuerzaBruta());
    }

    // menu pequeno para calcular a mano: proteina P(100), guarniciones A(50) B(200), bebidas C(10) D(300)
    private Menu menuPequeno() throws Exception {
        Menu m = new Menu();
        m.desactivarCategoria(m.buscarCategoria("Ensaladas"));
        m.desactivarCategoria(m.buscarCategoria("Postres"));
        m.agregarOpcion("P", m.buscarCategoria("Proteínas"), "x", 100, 1000);
        m.agregarOpcion("A", m.buscarCategoria("Guarniciones"), "x", 50, 500);
        m.agregarOpcion("B", m.buscarCategoria("Guarniciones"), "x", 200, 500);
        m.agregarOpcion("C", m.buscarCategoria("Bebidas"), "x", 10, 500);
        m.agregarOpcion("D", m.buscarCategoria("Bebidas"), "x", 300, 500);
        return m;
    }

    @Test
    void resultadoCalculadoAMano() throws Exception {
        Menu m = menuPequeno();
        // P+A+C=160, P+B+C=310, P+A+D=450, P+B+D=600
        ResultadoRecomendacion r = poda.recomendar(m, m.obtener(1), 350);
        assertEquals(2, r.getCombinaciones().size());
        assertEquals(160, r.getCombinaciones().get(0).getTotalCalorias());
        assertEquals(310, r.getCombinaciones().get(1).getTotalCalorias());
    }

    @Test
    void elLimiteEsInclusivo() throws Exception {
        Menu m = menuPequeno();
        assertEquals(2, poda.recomendar(m, m.obtener(1), 310).getCombinaciones().size());
        assertEquals(1, poda.recomendar(m, m.obtener(1), 309).getCombinaciones().size());
    }

    @Test
    void todasLasCombinacionesTienenUnaOpcionPorCategoriaYNoPasanElLimite() throws Exception {
        OpcionMenu pechuga = demo.obtener(5);
        ResultadoRecomendacion r = poda.recomendar(demo, pechuga, 800);
        assertTrue(r.hayResultados());
        for (Combinacion c : r.getCombinaciones()) {
            assertTrue(c.getTotalCalorias() <= 800);
            assertEquals(4, c.getOpciones().size());   // guarnicion, ensalada, postre y bebida
            assertEquals(pechuga, c.getProteina());
            Set<Categoria> categorias = new HashSet<>();
            for (OpcionMenu o : c.getOpciones()) categorias.add(o.getCategoria());
            assertEquals(4, categorias.size());        // ninguna categoria repetida
        }
    }

    @Test
    void laPodaDaLoMismoQueLaFuerzaBruta() throws Exception {
        for (int proteinaId : new int[]{5, 6, 7, 8}) {
            for (int limite : new int[]{0, 500, 640, 700, 800, 1000, 5000}) {
                OpcionMenu proteina = demo.obtener(proteinaId);
                List<Combinacion> a = poda.recomendar(demo, proteina, limite).getCombinaciones();
                List<Combinacion> b = fuerzaBruta.recomendar(demo, proteina, limite).getCombinaciones();
                assertEquals(textos(b), textos(a));
            }
        }
    }

    @Test
    void sinLimiteRealSeObtieneElProductoCompleto() throws Exception {
        // 4 guarniciones x 3 ensaladas x 4 postres x 4 bebidas = 192
        assertEquals(192, poda.recomendar(demo, demo.obtener(5), 100000).getCombinaciones().size());
    }

    @Test
    void noHayCombinacionesRepetidas() throws Exception {
        List<Combinacion> lista = poda.recomendar(demo, demo.obtener(5), 100000).getCombinaciones();
        assertEquals(lista.size(), new HashSet<>(textos(lista)).size());
    }

    @Test
    void elOrdenEsPorCaloriasAscendentes() throws Exception {
        List<Combinacion> lista = poda.recomendar(demo, demo.obtener(6), 1000).getCombinaciones();
        for (int i = 1; i < lista.size(); i++) {
            assertTrue(lista.get(i - 1).getTotalCalorias() <= lista.get(i).getTotalCalorias());
        }
    }

    @Test
    void elOrdenEsDeterminista() throws Exception {
        List<String> uno = textos(poda.recomendar(demo, demo.obtener(5), 900).getCombinaciones());
        List<String> dos = textos(poda.recomendar(demo, demo.obtener(5), 900).getCombinaciones());
        assertEquals(uno, dos);
    }

    @Test
    void limiteMuyBajoExplicaPorQueNoHayResultados() throws Exception {
        ResultadoRecomendacion r = poda.recomendar(demo, demo.obtener(5), 100);
        assertFalse(r.hayResultados());
        assertTrue(r.getMensaje().contains("630"));   // 280 + 180 + 90 + 80 + 0
    }

    @Test
    void categoriaSinOpcionesDisponiblesExplicaElMotivo() throws Exception {
        for (OpcionMenu o : demo.buscarPorCategoria(demo.buscarCategoria("Postres"))) {
            demo.desactivarOpcion(o.getId());
        }
        ResultadoRecomendacion r = poda.recomendar(demo, demo.obtener(5), 5000);
        assertFalse(r.hayResultados());
        assertTrue(r.getMensaje().contains("Postres"));
    }

    @Test
    void unaCategoriaDesactivadaNoEsRequerida() throws Exception {
        demo.desactivarCategoria(demo.buscarCategoria("Postres"));
        List<Combinacion> lista = poda.recomendar(demo, demo.obtener(5), 5000).getCombinaciones();
        assertEquals(48, lista.size());   // 4 x 3 x 4
        assertEquals(3, lista.get(0).getOpciones().size());
    }

    @Test
    void elClientePuedeEscogerLasCategorias() throws Exception {
        List<Categoria> solo = List.of(demo.buscarCategoria("Guarniciones"), demo.buscarCategoria("Bebidas"));
        List<Combinacion> lista = poda.recomendar(demo, demo.obtener(5), 5000, solo).getCombinaciones();
        assertEquals(16, lista.size());   // 4 x 4
    }

    @Test
    void lasOpcionesNoDisponiblesNoEntran() throws Exception {
        demo.desactivarOpcion(1);   // arroz blanco
        for (Combinacion c : poda.recomendar(demo, demo.obtener(5), 5000).getCombinaciones()) {
            for (OpcionMenu o : c.getOpciones()) {
                assertFalse(o.getId() == 1);
            }
        }
    }

    @Test
    void datosInvalidosLanzanExcepcion() {
        assertThrows(DatoInvalidoException.class, () -> poda.recomendar(demo, demo.obtener(5), -1));
        assertThrows(DatoInvalidoException.class, () -> poda.recomendar(demo, null, 500));
        // el arroz (id 1) es guarnicion, no proteina
        assertThrows(DatoInvalidoException.class, () -> poda.recomendar(demo, demo.obtener(1), 500));
    }

    @Test
    void proteinaNoDisponibleLanzaExcepcion() throws Exception {
        demo.desactivarOpcion(5);
        assertThrows(DatoInvalidoException.class, () -> poda.recomendar(demo, demo.obtener(5), 800));
    }

    private static List<String> textos(List<Combinacion> lista) {
        List<String> r = new ArrayList<>();
        for (Combinacion c : lista) r.add(c.toString());
        return r;
    }
}
