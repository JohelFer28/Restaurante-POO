package restaurante;

import restaurante.datos.DatosDemo;
import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;
import restaurante.excepciones.RestauranteException;

import java.util.List;
import java.util.Map;

// por ahora es una demo de consola de la parte 1, despues lo reemplaza la GUI
public class Main {
    public static void main(String[] args) {
        Menu menu = DatosDemo.crearMenuDemo();

        System.out.println("=== MENÚ POR CATEGORÍA ===");
        for (Map.Entry<Categoria, List<OpcionMenu>> e : menu.listarPorCategoria(true).entrySet()) {
            System.out.println("\n-- " + e.getKey() + " --");
            for (OpcionMenu o : e.getValue()) {
                System.out.println("  " + o);
            }
        }

        try {
            System.out.println("\n=== Búsqueda por nombre: \"ensalada\" ===");
            menu.buscarPorNombre("ensalada").forEach(o -> System.out.println("  " + o));

            System.out.println("\n=== Calorías entre 0 y 100 ===");
            menu.buscarPorRangoCalorias(0, 100).forEach(o -> System.out.println("  " + o));

            System.out.println("\n=== Precio negativo (debe fallar) ===");
            menu.agregarOpcion("Error", menu.buscarCategoria("Postres"), "x", 100, -5);
        } catch (RestauranteException e) {
            // aqui la GUI va a mostrar el mensaje en un cuadro de dialogo
            System.out.println("  Error controlado: " + e.getMessage());
        }
    }
}
