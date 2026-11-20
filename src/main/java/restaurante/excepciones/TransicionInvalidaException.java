package restaurante.excepciones;

// operacion que el estado actual de la mesa no permite
// ejemplo: pedir la cuenta de una mesa que esta disponible
public class TransicionInvalidaException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
