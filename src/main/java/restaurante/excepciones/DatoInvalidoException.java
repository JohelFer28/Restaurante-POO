package restaurante.excepciones;

/**
 * Se lanza cuando un dato no cumple una regla (precio negativo, nombre vacio, rango invertido...).
 * La lanza el objeto del dominio que es dueno de la regla; la GUI la atrapa y muestra el mensaje.
 */
public class DatoInvalidoException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
