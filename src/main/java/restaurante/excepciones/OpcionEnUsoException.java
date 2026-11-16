package restaurante.excepciones;

/**
 * Se lanza al intentar eliminar una opcion que ya aparece en algun pedido.
 * Es la "restriccion de integridad" del enunciado: no se puede borrar algo
 * que el historial de ventas necesita. La alternativa es desactivarla.
 */
public class OpcionEnUsoException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public OpcionEnUsoException(String nombre) {
        super("No se puede eliminar \"" + nombre + "\" porque ya fue pedida. Puede desactivarla en su lugar.");
    }
}
