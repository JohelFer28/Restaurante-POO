package restaurante;

import restaurante.datos.DatosDemo;
import restaurante.dominio.Categoria;
import restaurante.dominio.Mesa;
import restaurante.dominio.Menu;
import restaurante.dominio.Mesero;
import restaurante.dominio.OpcionMenu;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.RestauranteException;

import java.util.List;
import java.util.Map;

// por ahora es una demo de consola de las partes 1 y 2, despues la reemplaza la GUI
public class Main {
    public static void main(String[] args) {
        Restaurante restaurante = DatosDemo.crearRestauranteDemo();
        demoMenu(restaurante.getMenu());
        demoMesas(restaurante);
    }

    private static void demoMenu(Menu menu) {
        System.out.println("=== MENU POR CATEGORIA ===");
        for (Map.Entry<Categoria, List<OpcionMenu>> e : menu.listarPorCategoria(true).entrySet()) {
            System.out.println("\n-- " + e.getKey() + " --");
            for (OpcionMenu o : e.getValue()) {
                System.out.println("  " + o);
            }
        }

        try {
            System.out.println("\n=== Busqueda por nombre: \"ensalada\" ===");
            menu.buscarPorNombre("ensalada").forEach(o -> System.out.println("  " + o));

            System.out.println("\n=== Precio negativo (debe fallar) ===");
            menu.agregarOpcion("Error", menu.buscarCategoria("Postres"), "x", 100, -5);
        } catch (RestauranteException e) {
            System.out.println("  Error controlado: " + e.getMessage());
        }
    }

    private static void demoMesas(Restaurante restaurante) {
        System.out.println("\n\n=== MESAS Y VISITAS ===");
        try {
            Mesero mesero = new Mesero("Carlos");
            restaurante.abrirVisita(1, mesero, List.of("Ana", "Luis", "Marta"));
            mostrarMesas(restaurante);

            System.out.println("\n-- abrir otra vez la mesa 1 (debe fallar) --");
            try {
                restaurante.abrirVisita(1, mesero, List.of("Pedro"));
            } catch (RestauranteException e) {
                System.out.println("  Error controlado: " + e.getMessage());
            }

            System.out.println("\n-- alias repetido en la mesa 2 (debe fallar y dejarla libre) --");
            try {
                restaurante.abrirVisita(2, mesero, List.of("Ana", "ana"));
            } catch (RestauranteException e) {
                System.out.println("  Error controlado: " + e.getMessage());
            }
            System.out.println("  " + restaurante.obtenerMesa(2));

            System.out.println("\n-- ciclo completo de la mesa 1 --");
            restaurante.solicitarCuenta(1);
            System.out.println("  " + restaurante.obtenerMesa(1));
            restaurante.cerrarVisita(1);
            System.out.println("  " + restaurante.obtenerMesa(1));
            System.out.println("\n" + restaurante.getHistorial().get(0).resumen());
        } catch (RestauranteException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private static void mostrarMesas(Restaurante restaurante) {
        for (Mesa m : restaurante.getMesas()) {
            System.out.println("  " + m);
        }
    }
}
