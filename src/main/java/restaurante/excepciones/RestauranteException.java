package restaurante.excepciones;

// base de todas las excepciones del sistema
// es checked (extends Exception), asi el compilador obliga a manejarlas
public class RestauranteException extends Exception {
    private static final long serialVersionUID = 1L;

    public RestauranteException(String mensaje) {
        super(mensaje);
    }
}
