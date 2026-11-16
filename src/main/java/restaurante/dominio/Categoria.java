package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Texto;

/**
 * Categoria del menu (Guarniciones, Proteinas, ...).
 * Es una clase y no un enum porque el enunciado permite agregar categorias nuevas en ejecucion.
 * El constructor y cambiarActiva() son de paquete: solo Menu puede crear y activar categorias.
 */
public final class Categoria {
    private final String nombre;
    private boolean activa = true;

    Categoria(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre de la categoría no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() { return nombre; }

    public boolean estaActiva() { return activa; }

    void cambiarActiva(boolean activa) { this.activa = activa; }

    /** Dos categorias son iguales si tienen el mismo nombre (sin importar tildes ni mayusculas). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Categoria)) return false;
        return Texto.normalizar(nombre).equals(Texto.normalizar(((Categoria) otro).nombre));
    }

    @Override
    public int hashCode() { return Texto.normalizar(nombre).hashCode(); }

    @Override
    public String toString() { return nombre; }
}
