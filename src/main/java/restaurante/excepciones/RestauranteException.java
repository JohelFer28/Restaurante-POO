package restaurante.excepciones;

/**
 * Clase base de todas las excepciones propias del sistema.
 * Es "checked" (extiende Exception): el compilador obliga a quien llama
 * a atenderla o declararla, asi nadie ignora un error de negocio por accidente.
 */
public class RestauranteException extends Exception {
    private static final long serialVersionUID = 1L;

    public RestauranteException(String mensaje) {
        super(mensaje);
    }
}
