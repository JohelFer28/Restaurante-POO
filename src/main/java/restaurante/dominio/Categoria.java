package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Texto;

// es clase y no enum porque se pueden agregar categorias nuevas mientras corre el programa
public final class Categoria {
    private final String nombre;
    private boolean activa = true;

    // constructor y cambiarActiva sin public: solo Menu las usa
    Categoria(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre de la categoría no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() { return nombre; }

    public boolean estaActiva() { return activa; }

    void cambiarActiva(boolean activa) { this.activa = activa; }

    // dos categorias son iguales si tienen el mismo nombre (sin tildes ni mayusculas)
    // equals y hashCode siempre se definen juntos
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
