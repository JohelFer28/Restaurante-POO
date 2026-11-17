package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Formato;

// una opcion individual del menu, por ejemplo "Arroz blanco"
// atributos privados y sin setters sueltos, los cambios pasan por modificar()
public class OpcionMenu {
    private final int id;
    private final Categoria categoria;
    private String nombre;
    private String descripcion;
    private int calorias;   // por porcion
    private int precio;     // en colones
    private boolean disponible = true;
    private boolean usadaEnPedidos = false;

    // sin public: solo Menu crea opciones, asi controla los ids
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

    // las reglas estan aqui para usarlas en el constructor y en modificar()
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

    // primero valida y despues asigna, si algo falla el objeto queda igual que antes
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

    // lo va a llamar Pedido al registrar la opcion, desde ahi ya no se puede eliminar
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
