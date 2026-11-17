package restaurante.excepciones;

// se pide una opcion por un id que no existe en el menu
public class OpcionNoEncontradaException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public OpcionNoEncontradaException(int id) {
        super("No existe una opción de menú con id " + id + ".");
    }
}
