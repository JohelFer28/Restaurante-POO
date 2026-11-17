package restaurante.excepciones;

/** Se lanza cuando se pide una opcion del menu por un id que no existe. */
public class OpcionNoEncontradaException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public OpcionNoEncontradaException(int id) {
        super("No existe una opción de menú con id " + id + ".");
    }
}
