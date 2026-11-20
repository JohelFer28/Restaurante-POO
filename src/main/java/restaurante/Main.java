package restaurante;

import restaurante.datos.DatosDemo;
import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.Mesa;
import restaurante.dominio.Mesero;
import restaurante.dominio.ModalidadCuenta;
import restaurante.dominio.OpcionMenu;
import restaurante.dominio.PagoEfectivo;
import restaurante.dominio.PagoTarjeta;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.RestauranteException;
import restaurante.recomendacion.Combinacion;
import restaurante.recomendacion.RecomendacionPorPoda;
import restaurante.recomendacion.ResultadoRecomendacion;
import restaurante.recomendacion.ServicioRecomendacion;

import java.util.List;
import java.util.Map;

// por ahora es una demo de consola de las partes 1 a 5, despues la reemplaza la GUI
public class Main {
    public static void main(String[] args) {
        Restaurante restaurante = DatosDemo.crearRestauranteDemo();
        demoMenu(restaurante.getMenu());
        demoVisita(restaurante);
        demoRecomendacion(restaurante.getMenu());
    }

    private static void demoMenu(Menu menu) {
        System.out.println("=== MENU POR CATEGORIA ===");
        for (Map.Entry<Categoria, List<OpcionMenu>> e : menu.listarPorCategoria(true).entrySet()) {
            System.out.println("\n-- " + e.getKey() + " --");
            for (OpcionMenu o : e.getValue()) {
                System.out.println("  " + o);
            }
        }
    }

    private static void demoVisita(Restaurante restaurante) {
        System.out.println("\n\n=== VISITA COMPLETA (mesa 1) ===");
        try {
            restaurante.abrirVisita(1, new Mesero("Carlos"), List.of("Ana", "Luis"));

            restaurante.agregarPedido(1, "Ana", 5, 1);    // pechuga
            restaurante.agregarPedido(1, "Ana", 1, 2);    // 2 arroz
            restaurante.agregarPedido(1, "Luis", 6, 1);   // filete
            restaurante.agregarPedido(1, "Luis", 17, 2);  // 2 limonadas
            restaurante.agregarPedido(1, "Luis", 15, 1);  // brownie (se anula luego)
            restaurante.anularPedido(1, 5, "El cliente se arrepintió");

            System.out.println("\n-- error esperado: cantidad invalida --");
            intentar(() -> restaurante.agregarPedido(1, "Ana", 1, 0));
            System.out.println("\n-- error esperado: comensal que no existe --");
            intentar(() -> restaurante.agregarPedido(1, "Pedro", 1, 1));

            System.out.println("\n" + restaurante.obtenerMesa(1).getVisitaActual().resumen());

            restaurante.solicitarCuenta(1, ModalidadCuenta.INDIVIDUAL);
            System.out.println("\n-- error esperado: pedir despues de solicitar la cuenta --");
            intentar(() -> restaurante.agregarPedido(1, "Ana", 1, 1));
            System.out.println("\n-- error esperado: pagar mas de lo que debe Ana --");
            intentar(() -> restaurante.registrarPago(1, "Ana", 999999, new PagoEfectivo()));
            System.out.println("\n-- error esperado: cerrar con saldo pendiente --");
            intentar(() -> restaurante.cerrarVisita(1));

            int deudaAna = restaurante.obtenerMesa(1).getVisitaActual().saldoComensal("Ana");
            int deudaLuis = restaurante.obtenerMesa(1).getVisitaActual().saldoComensal("Luis");
            restaurante.registrarPago(1, "Ana", deudaAna, new PagoEfectivo());
            restaurante.registrarPago(1, "Luis", deudaLuis, new PagoTarjeta());
            restaurante.cerrarVisita(1);

            System.out.println("\n" + restaurante.getHistorial().get(0).resumen());
            System.out.println("\n" + restaurante.obtenerMesa(1));
        } catch (RestauranteException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private static void demoRecomendacion(Menu menu) {
        System.out.println("\n\n=== COMIDAS COMPLETAS POR CALORIAS ===");
        ServicioRecomendacion servicio = new ServicioRecomendacion(new RecomendacionPorPoda());
        try {
            OpcionMenu pechuga = menu.obtener(5);
            ResultadoRecomendacion r = servicio.recomendar(menu, pechuga, 800);
            System.out.println(r.getMensaje());
            int mostradas = 0;
            for (Combinacion c : r.getCombinaciones()) {
                System.out.println("  " + c);
                if (++mostradas == 5) break;
            }

            System.out.println("\n-- limite muy bajo --");
            System.out.println(servicio.recomendar(menu, pechuga, 100).getMensaje());
        } catch (RestauranteException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // ejecuta algo que debe fallar y muestra el mensaje de la excepcion
    private static void intentar(Accion accion) {
        try {
            accion.ejecutar();
            System.out.println("  (no fallo, y deberia)");
        } catch (RestauranteException e) {
            System.out.println("  Error controlado: " + e.getMessage());
        }
    }

    private interface Accion {
        void ejecutar() throws RestauranteException;
    }
}
