package restaurante.excepciones;

// no se puede eliminar una opcion que ya aparece en un pedido
// el historial la necesita, lo que se hace es desactivarla
public class OpcionEnUsoException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public OpcionEnUsoException(String nombre) {
        super("No se puede eliminar \"" + nombre + "\" porque ya fue pedida. Puede desactivarla en su lugar.");
    }
}
