package restaurante.datos;

import restaurante.dominio.Menu;
import restaurante.excepciones.DatoInvalidoException;

// menu precargado para la demostracion (el enunciado pide que el sistema ya traiga opciones)
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
}
