package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.OpcionEnUsoException;
import restaurante.excepciones.OpcionNoEncontradaException;
import restaurante.util.Texto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// administra las categorias y las opciones del menu
// es la unica clase que crea y elimina opciones, asi controla ids unicos y la integridad
// los metodos de listado devuelven copias para que nadie dane el menu desde afuera
public class Menu {
    // la categoria de proteinas es especial: el filtro calorico parte de una proteina elegida
    public static final String NOMBRE_PROTEINAS = "Proteínas";

    private final Map<Integer, OpcionMenu> opciones = new LinkedHashMap<>(); // mantiene el orden en que se agregan
    private final List<Categoria> categorias = new ArrayList<>();
    private int siguienteId = 1;

    // arranca con las 5 categorias minimas del enunciado
    public Menu() {
        try {
            for (String nombre : new String[]{"Guarniciones", NOMBRE_PROTEINAS, "Ensaladas", "Postres", "Bebidas"}) {
                categorias.add(new Categoria(nombre));
            }
        } catch (DatoInvalidoException e) {
            throw new IllegalStateException("Categorías por defecto inválidas", e); // no deberia pasar
        }
    }

    // categorias

    public Categoria agregarCategoria(String nombre) throws DatoInvalidoException {
        Categoria nueva = new Categoria(nombre);
        if (categorias.contains(nueva)) {
            throw new DatoInvalidoException("Ya existe la categoría \"" + nueva + "\".");
        }
        categorias.add(nueva);
        return nueva;
    }

    public Categoria buscarCategoria(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el nombre de la categoría.");
        }
        for (Categoria c : categorias) {
            if (Texto.normalizar(c.getNombre()).equals(Texto.normalizar(nombre))) {
                return c;
            }
        }
        throw new DatoInvalidoException("No existe la categoría \"" + nombre.trim() + "\".");
    }

    public void activarCategoria(Categoria c) { c.cambiarActiva(true); }

    public void desactivarCategoria(Categoria c) { c.cambiarActiva(false); }

    public List<Categoria> getCategorias() { return List.copyOf(categorias); }

    public List<Categoria> getCategoriasActivas() {
        List<Categoria> activas = new ArrayList<>();
        for (Categoria c : categorias) {
            if (c.estaActiva()) activas.add(c);
        }
        return activas;
    }

    // crud de opciones

    public OpcionMenu agregarOpcion(String nombre, Categoria categoria, String descripcion,
                                    int calorias, int precio) throws DatoInvalidoException {
        if (categoria == null || !categorias.contains(categoria)) {
            throw new DatoInvalidoException("La categoría no pertenece a este menú.");
        }
        // si el constructor lanza excepcion, siguienteId no se incrementa
        OpcionMenu nueva = new OpcionMenu(siguienteId, nombre, categoria, descripcion, calorias, precio);
        opciones.put(siguienteId, nueva);
        siguienteId++;
        return nueva;
    }

    public OpcionMenu obtener(int id) throws OpcionNoEncontradaException {
        OpcionMenu o = opciones.get(id);
        if (o == null) throw new OpcionNoEncontradaException(id);
        return o;
    }

    public void modificarOpcion(int id, String nombre, String descripcion, int calorias, int precio)
            throws OpcionNoEncontradaException, DatoInvalidoException {
        obtener(id).modificar(nombre, descripcion, calorias, precio);
    }

    public void activarOpcion(int id) throws OpcionNoEncontradaException { obtener(id).activar(); }

    public void desactivarOpcion(int id) throws OpcionNoEncontradaException { obtener(id).desactivar(); }

    // solo se elimina si nunca fue pedida, si ya fue pedida hay que desactivarla
    public void eliminarOpcion(int id) throws OpcionNoEncontradaException, OpcionEnUsoException {
        OpcionMenu o = obtener(id);
        if (o.estaUsadaEnPedidos()) {
            throw new OpcionEnUsoException(o.getNombre());
        }
        opciones.remove(id);
    }

    // listados

    public List<OpcionMenu> listarTodas() {
        return List.copyOf(opciones.values());
    }

    // opciones agrupadas por categoria (incluye las vacias), el mapa devuelto es de solo lectura
    public Map<Categoria, List<OpcionMenu>> listarPorCategoria(boolean soloDisponibles) {
        Map<Categoria, List<OpcionMenu>> resultado = new LinkedHashMap<>();
        for (Categoria c : categorias) {
            resultado.put(c, new ArrayList<>());
        }
        for (OpcionMenu o : opciones.values()) {
            if (!soloDisponibles || o.estaDisponible()) {
                resultado.get(o.getCategoria()).add(o);
            }
        }
        return Collections.unmodifiableMap(resultado);
    }

    // opciones disponibles de una categoria, lo va a usar el filtro calorico
    public List<OpcionMenu> opcionesDisponibles(Categoria categoria) {
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getCategoria().equals(categoria) && o.estaDisponible()) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    // busquedas (4 criterios)

    // 1) el nombre contiene el texto, sin importar mayusculas ni tildes
    public List<OpcionMenu> buscarPorNombre(String texto) throws DatoInvalidoException {
        if (texto == null || texto.isBlank()) {
            throw new DatoInvalidoException("Escriba un texto para buscar.");
        }
        String buscado = Texto.normalizar(texto);
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (Texto.normalizar(o.getNombre()).contains(buscado)) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    // 2) todas las opciones de una categoria (disponibles o no)
    public List<OpcionMenu> buscarPorCategoria(Categoria categoria) throws DatoInvalidoException {
        if (categoria == null) throw new DatoInvalidoException("Debe elegir una categoría.");
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getCategoria().equals(categoria)) resultado.add(o);
        }
        return resultado;
    }

    // 3) calorias entre min y max, ambos incluidos
    public List<OpcionMenu> buscarPorRangoCalorias(int min, int max) throws DatoInvalidoException {
        validarRango(min, max, "calorías");
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getCalorias() >= min && o.getCalorias() <= max) resultado.add(o);
        }
        return resultado;
    }

    // 4) precio entre min y max, ambos incluidos
    public List<OpcionMenu> buscarPorRangoPrecio(int min, int max) throws DatoInvalidoException {
        validarRango(min, max, "precio");
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getPrecio() >= min && o.getPrecio() <= max) resultado.add(o);
        }
        return resultado;
    }

    private static void validarRango(int min, int max, String que) throws DatoInvalidoException {
        if (min < 0 || max < 0) {
            throw new DatoInvalidoException("El rango de " + que + " no puede tener valores negativos.");
        }
        if (min > max) {
            throw new DatoInvalidoException("En el rango de " + que + ", el mínimo no puede ser mayor que el máximo.");
        }
    }
}
