package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Formato;

/**
 * Una opcion individual del menu (ej: "Arroz blanco").
 *
 * ENCAPSULAMIENTO: todos los atributos son privados y no hay setters sueltos.
 * Para cambiar datos se usa modificar(), que valida TODO antes de tocar nada
 * (si algun dato es invalido, el objeto queda exactamente como estaba).
 * El constructor es de paquete: solo Menu crea opciones, asi controla los ids.
 */
public class OpcionMenu {
    private final int id;
    private final Categoria categoria;
    private String nombre;
    private String descripcion;
    private int calorias;     // por porcion
    private int precio;       // en colones
    private boolean disponible = true;
    private boolean usadaEnPedidos = false;

    OpcionMenu(int id, String nombre, Categoria categoria, String descripcion, int calorias, int precio)
            throws DatoInvalidoException {
        if (categoria == null) {
            throw new DatoInvalidoException("La opción debe pertenecer a una categoría.");
        }
        validar(nombre, descripcion, calorias, precio);
        this.id = id;
        this.categoria = categoria;
        this.nombre = nombre.trim();
        this.descripcion = descripcion.trim();
        this.calorias = calorias;
        this.precio = precio;
    }

    /** Reglas de validacion centralizadas: las usan el constructor y modificar(). */
    private static void validar(String nombre, String descripcion, int calorias, int precio)
            throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre no puede estar vacío.");
        }
        if (descripcion == null) {
            throw new DatoInvalidoException("La descripción no puede ser nula.");
        }
        if (calorias < 0) {
            throw new DatoInvalidoException("Las calorías no pueden ser negativas.");
        }
        if (precio < 0) {
            throw new DatoInvalidoException("El precio no puede ser negativo.");
        }
    }

    /** Valida primero y asigna despues: nunca queda a medio modificar. */
    public void modificar(String nombre, String descripcion, int calorias, int precio)
            throws DatoInvalidoException {
        validar(nombre, descripcion, calorias, precio);
        this.nombre = nombre.trim();
        this.descripcion = descripcion.trim();
        this.calorias = calorias;
        this.precio = precio;
    }

    public void activar() { disponible = true; }

    public void desactivar() { disponible = false; }

    /** Lo llamara Pedido (Parte 3) al registrar esta opcion; desde entonces no se puede eliminar. */
    public void marcarComoUsada() { usadaEnPedidos = true; }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public int getCalorias() { return calorias; }
    public int getPrecio() { return precio; }
    public boolean estaDisponible() { return disponible; }
    public boolean estaUsadaEnPedidos() { return usadaEnPedidos; }

    @Override
    public String toString() {
        return "#" + id + " " + nombre + " [" + categoria + "] " + calorias + " kcal, "
                + Formato.colones(precio) + (disponible ? "" : " (no disponible)");
    }
}
