package restaurante.datos;

import restaurante.dominio.Menu;
<<<<<<< HEAD
import restaurante.dominio.Mesero;
import restaurante.dominio.ModalidadCuenta;
import restaurante.dominio.PagoEfectivo;
import restaurante.dominio.PagoSinpe;
import restaurante.dominio.PagoTarjeta;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.RestauranteException;
import restaurante.observador.BitacoraEventos;

import java.util.List;

// datos precargados para demostracion y pruebas
=======
import restaurante.dominio.Restaurante;
import restaurante.excepciones.DatoInvalidoException;

// menu precargado para la demostracion (el enunciado pide que el sistema ya traiga opciones)
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
public final class DatosDemo {
    private DatosDemo() { }

    public static Menu crearMenuDemo() {
        Menu m = new Menu();
        try {
            // orden: nombre, categoria, descripcion, calorias, precio en colones
            m.agregarOpcion("Arroz blanco", m.buscarCategoria("Guarniciones"), "Arroz blanco cocido al vapor", 180, 1500);
            m.agregarOpcion("Puré de papa", m.buscarCategoria("Guarniciones"), "Puré cremoso con mantequilla", 220, 1800);
            m.agregarOpcion("Papas fritas", m.buscarCategoria("Guarniciones"), "Papas en bastones fritas", 365, 2200);
            m.agregarOpcion("Gallo pinto", m.buscarCategoria("Guarniciones"), "Arroz y frijoles tradicionales", 250, 1700);

            m.agregarOpcion("Pechuga a la plancha", m.buscarCategoria("Proteínas"), "Pechuga de pollo sazonada", 280, 4500);
            m.agregarOpcion("Filete de res", m.buscarCategoria("Proteínas"), "Filete de res a la parrilla", 420, 6500);
            m.agregarOpcion("Salmón al horno", m.buscarCategoria("Proteínas"), "Salmón con limón y hierbas", 350, 7500);
            m.agregarOpcion("Tofu salteado", m.buscarCategoria("Proteínas"), "Tofu con vegetales salteados", 190, 4000);

            m.agregarOpcion("Ensalada César", m.buscarCategoria("Ensaladas"), "Lechuga, crutones y aderezo césar", 210, 3000);
            m.agregarOpcion("Ensalada mixta", m.buscarCategoria("Ensaladas"), "Lechuga, tomate y pepino", 90, 2500);
            m.agregarOpcion("Ensalada de frutas", m.buscarCategoria("Ensaladas"), "Frutas frescas de temporada", 120, 2800);

            m.agregarOpcion("Flan de coco", m.buscarCategoria("Postres"), "Flan casero con coco", 260, 2200);
            m.agregarOpcion("Helado de vainilla", m.buscarCategoria("Postres"), "Dos bolas de helado", 200, 2000);
            m.agregarOpcion("Gelatina", m.buscarCategoria("Postres"), "Gelatina de frutas", 80, 1200);
            m.agregarOpcion("Brownie", m.buscarCategoria("Postres"), "Brownie de chocolate tibio", 380, 2600);

            m.agregarOpcion("Agua natural", m.buscarCategoria("Bebidas"), "Botella de agua", 0, 1000);
            m.agregarOpcion("Limonada", m.buscarCategoria("Bebidas"), "Limonada natural", 110, 1500);
            m.agregarOpcion("Refresco natural", m.buscarCategoria("Bebidas"), "Fruta de temporada", 150, 1600);
            m.agregarOpcion("Café americano", m.buscarCategoria("Bebidas"), "Café negro sin azúcar", 5, 1300);
        } catch (DatoInvalidoException e) {
            throw new IllegalStateException("Los datos de demostración son inválidos", e);
        }
        return m;
    }

<<<<<<< HEAD
    // restaurante limpio (8 mesas y menu precargado) usado por los tests unitarios
=======
    // restaurante con 8 mesas y el menu de demostracion
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    public static Restaurante crearRestauranteDemo() {
        try {
            return new Restaurante(8, crearMenuDemo());
        } catch (DatoInvalidoException e) {
            throw new IllegalStateException("Los datos de demostración son inválidos", e);
        }
    }
<<<<<<< HEAD

    // restaurante con visitas cerradas precargadas para que Estadisticas e Historial tengan datos al iniciar la GUI
    public static Restaurante crearRestauranteConHistorialDemo(BitacoraEventos bitacora) {
        Restaurante r = crearRestauranteDemo();
        if (bitacora != null) {
            r.agregarObservador(bitacora);
        }
        try {
            // Visita historica 1 (Mesa 2 - cuenta conjunta en Tarjeta)
            r.abrirVisita(2, new Mesero("Carlos"), List.of("Valeria", "Diego"));
            r.agregarPedido(2, "Valeria", 7, 1);  // Salmon al horno (7500)
            r.agregarPedido(2, "Valeria", 10, 1); // Ensalada mixta (2500)
            r.agregarPedido(2, "Valeria", 16, 1); // Agua natural (1000)
            r.agregarPedido(2, "Diego", 6, 1);    // Filete de res (6500)
            r.agregarPedido(2, "Diego", 3, 1);    // Papas fritas (2200)
            r.agregarPedido(2, "Diego", 17, 1);   // Limonada (1500)
            r.solicitarCuenta(2, ModalidadCuenta.CONJUNTA);
            r.registrarPago(2, null, 21200, new PagoTarjeta());
            r.cerrarVisita(2);

            // Visita historica 2 (Mesa 4 - pagos individuales con SINPE y Efectivo)
            r.abrirVisita(4, new Mesero("Sofía"), List.of("Mateo", "Camila", "Andrés"));
            r.agregarPedido(4, "Mateo", 5, 1);    // Pechuga a la plancha (4500)
            r.agregarPedido(4, "Mateo", 1, 1);    // Arroz blanco (1500)
            r.agregarPedido(4, "Camila", 8, 1);   // Tofu salteado (4000)
            r.agregarPedido(4, "Camila", 9, 1);   // Ensalada Cesar (3000)
            r.agregarPedido(4, "Camila", 19, 1);  // Cafe americano (1300)
            r.agregarPedido(4, "Andrés", 5, 1);   // Pechuga a la plancha (4500)
            r.agregarPedido(4, "Andrés", 15, 1);  // Brownie (2600)
            r.agregarPedido(4, "Andrés", 18, 1);  // Refresco natural (1600)
            r.solicitarCuenta(4, ModalidadCuenta.INDIVIDUAL);
            r.registrarPago(4, "Mateo", 6000, new PagoSinpe());
            r.registrarPago(4, "Camila", 8300, new PagoTarjeta());
            r.registrarPago(4, "Andrés", 8700, new PagoEfectivo());
            r.cerrarVisita(4);

            // Visita historica 3 (Mesa 6 - grupo familiar)
            r.abrirVisita(6, new Mesero("Carlos"), List.of("Elena", "Roberto"));
            r.agregarPedido(6, "Elena", 5, 1);    // Pechuga (4500)
            r.agregarPedido(6, "Elena", 4, 1);    // Gallo pinto (1700)
            r.agregarPedido(6, "Elena", 12, 1);   // Flan de coco (2200)
            r.agregarPedido(6, "Roberto", 6, 1);  // Filete de res (6500)
            r.agregarPedido(6, "Roberto", 2, 1);  // Pure de papa (1800)
            r.agregarPedido(6, "Roberto", 17, 2); // 2 Limonadas (3000)
            r.solicitarCuenta(6, ModalidadCuenta.CONJUNTA);
            r.registrarPago(6, null, 10000, new PagoEfectivo());
            r.registrarPago(6, null, 9700, new PagoSinpe());
            r.cerrarVisita(6);
        } catch (RestauranteException e) {
            throw new IllegalStateException("Error preparando visitas demo", e);
        }
        return r;
    }
=======
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
}
