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

/**
 * El menu completo: administra categorias y opciones.
 * Es la UNICA clase que crea y elimina opciones, asi garantiza ids unicos y reglas de integridad.
 * Los metodos que devuelven listas devuelven COPIAS (o vistas de solo lectura),
 * por eso quien las reciba no puede danar el menu por dentro.
 */
public class Menu {
    private final Map<Integer, OpcionMenu> opciones = new LinkedHashMap<>(); // conserva orden de insercion
    private final List<Categoria> categorias = new ArrayList<>();
    private int siguienteId = 1;

    /** Crea el menu con las 5 categorias minimas que pide el enunciado. */
    public Menu() {
        try {
            for (String nombre : new String[]{"Guarniciones", "Proteínas", "Ensaladas", "Postres", "Bebidas"}) {
                categorias.add(new Categoria(nombre));
            }
        } catch (DatoInvalidoException e) {
            throw new IllegalStateException("Categorías por defecto inválidas", e); // no deberia pasar nunca
        }
    }

    // ------------------------------------------------------------ Categorias

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

    // ------------------------------------------------------------ CRUD de opciones

    public OpcionMenu agregarOpcion(String nombre, Categoria categoria, String descripcion,
                                    int calorias, int precio) throws DatoInvalidoException {
        if (categoria == null || !categorias.contains(categoria)) {
            throw new DatoInvalidoException("La categoría no pertenece a este menú.");
        }
        // Si el constructor lanza excepcion, siguienteId NO se incrementa (no se "gasta" un id).
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

    /** Elimina solo si la opcion nunca fue pedida; si ya fue pedida hay que desactivarla. */
    public void eliminarOpcion(int id) throws OpcionNoEncontradaException, OpcionEnUsoException {
        OpcionMenu o = obtener(id);
        if (o.estaUsadaEnPedidos()) {
            throw new OpcionEnUsoException(o.getNombre());
        }
        opciones.remove(id);
    }

    // ------------------------------------------------------------ Listados

    public List<OpcionMenu> listarTodas() {
        return List.copyOf(opciones.values());
    }

    /** Opciones agrupadas por categoria (incluye categorias vacias). El mapa devuelto no se puede modificar. */
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

    /** Opciones disponibles de una categoria. Lo usara el filtro calorico (Parte 5). */
    public List<OpcionMenu> opcionesDisponibles(Categoria categoria) {
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getCategoria().equals(categoria) && o.estaDisponible()) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    // ------------------------------------------------------------ Busquedas (4 criterios)

    /** Criterio 1: el nombre CONTIENE el texto, sin importar mayusculas ni tildes. */
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

    /** Criterio 2: todas las opciones (disponibles o no) de una categoria. */
    public List<OpcionMenu> buscarPorCategoria(Categoria categoria) throws DatoInvalidoException {
        if (categoria == null) throw new DatoInvalidoException("Debe elegir una categoría.");
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getCategoria().equals(categoria)) resultado.add(o);
        }
        return resultado;
    }

    /** Criterio 3: calorias entre min y max, ambos INCLUIDOS. */
    public List<OpcionMenu> buscarPorRangoCalorias(int min, int max) throws DatoInvalidoException {
        validarRango(min, max, "calorías");
        List<OpcionMenu> resultado = new ArrayList<>();
        for (OpcionMenu o : opciones.values()) {
            if (o.getCalorias() >= min && o.getCalorias() <= max) resultado.add(o);
        }
        return resultado;
    }

    /** Criterio 4: precio entre min y max, ambos INCLUIDOS. */
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
