package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;

// persona responsable de atender la mesa
public class Mesero {
    private final String nombre;

    public Mesero(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre del mesero no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
