package restaurante.datos;

import restaurante.dominio.Menu;
import restaurante.dominio.Mesero;
import restaurante.dominio.ModalidadCuenta;
import restaurante.dominio.OpcionMenu;
import restaurante.dominio.PagoEfectivo;
import restaurante.dominio.PagoSinpe;
import restaurante.dominio.PagoTarjeta;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.RestauranteException;
import restaurante.observador.BitacoraEventos;

import java.util.List;

// precarga el menu inicial y visitas cerradas para demostracion y pruebas
public final class DatosDemo {
    private DatosDemo() { }

    public static Menu crearMenuDemo() {
        Menu m = new Menu();
        try {
            // orden de parametros: nombre, categoria, descripcion, calorias, precio entero en colones
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

            // se dejan dos opciones desactivadas desde el inicio para probar filtros y bloqueo de pedidos
            OpcionMenu corvina = m.agregarOpcion("Corvina al ajillo", m.buscarCategoria("Proteínas"),
                    "Filete de corvina en salsa de ajo", 340, 6800);
            m.desactivarOpcion(corvina.getId());

            OpcionMenu tresLeches = m.agregarOpcion("Tres leches", m.buscarCategoria("Postres"),
                    "Bizcocho artesanal bañado en tres leches", 390, 2500);
            m.desactivarOpcion(tresLeches.getId());
        } catch (RestauranteException e) {
            throw new IllegalStateException("Los datos de demostración son inválidos", e);
        }
        return m;
    }

    // restaurante limpio con 8 mesas libres usado por los tests unitarios
    public static Restaurante crearRestauranteDemo() {
        try {
            return new Restaurante(8, crearMenuDemo());
        } catch (DatoInvalidoException e) {
            throw new IllegalStateException("Los datos de demostración son inválidos", e);
        }
    }

    // simula 3 visitas cerradas al arrancar la app para que estadisticas e historial tengan datos reales
    public static Restaurante crearRestauranteConHistorialDemo(BitacoraEventos bitacora) {
        Restaurante r = crearRestauranteDemo();
        if (bitacora != null) {
            r.agregarObservador(bitacora);
        }
        try {
            // visita 1 en mesa 2 con modalidad conjunta y pago en tarjeta
            r.abrirVisita(2, new Mesero("Carlos"), List.of("Valeria", "Diego"));
            r.agregarPedido(2, "Valeria", 7, 1);
            r.agregarPedido(2, "Valeria", 10, 1);
            r.agregarPedido(2, "Valeria", 16, 1);
            r.agregarPedido(2, "Diego", 6, 1);
            r.agregarPedido(2, "Diego", 3, 1);
            r.agregarPedido(2, "Diego", 17, 1);
            r.solicitarCuenta(2, ModalidadCuenta.CONJUNTA);
            r.registrarPago(2, null, 21200, new PagoTarjeta());
            r.cerrarVisita(2);

            // visita 2 en mesa 4 con pagos individuales por cada comensal
            r.abrirVisita(4, new Mesero("Sofía"), List.of("Mateo", "Camila", "Andrés"));
            r.agregarPedido(4, "Mateo", 5, 1);
            r.agregarPedido(4, "Mateo", 1, 1);
            r.agregarPedido(4, "Camila", 8, 1);
            r.agregarPedido(4, "Camila", 9, 1);
            r.agregarPedido(4, "Camila", 19, 1);
            r.agregarPedido(4, "Andrés", 5, 1);
            r.agregarPedido(4, "Andrés", 15, 1);
            r.agregarPedido(4, "Andrés", 18, 1);
            r.solicitarCuenta(4, ModalidadCuenta.INDIVIDUAL);
            r.registrarPago(4, "Mateo", 6000, new PagoSinpe());
            r.registrarPago(4, "Camila", 8300, new PagoTarjeta());
            r.registrarPago(4, "Andrés", 8700, new PagoEfectivo());
            r.cerrarVisita(4);

            // visita 3 en mesa 6 con pago mixto (efectivo y sinpe)
            r.abrirVisita(6, new Mesero("Carlos"), List.of("Elena", "Roberto"));
            r.agregarPedido(6, "Elena", 5, 1);
            r.agregarPedido(6, "Elena", 4, 1);
            r.agregarPedido(6, "Elena", 12, 1);
            r.agregarPedido(6, "Roberto", 6, 1);
            r.agregarPedido(6, "Roberto", 2, 1);
            r.agregarPedido(6, "Roberto", 17, 2);
            r.solicitarCuenta(6, ModalidadCuenta.CONJUNTA);
            r.registrarPago(6, null, 10000, new PagoEfectivo());
            r.registrarPago(6, null, 9700, new PagoSinpe());
            r.cerrarVisita(6);
        } catch (RestauranteException e) {
            throw new IllegalStateException("Error preparando visitas demo", e);
        }
        return r;
    }
}
