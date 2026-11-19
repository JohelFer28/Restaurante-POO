package restaurante.excepciones;

// pago que rompe una regla: monto no positivo, monto mayor al saldo, persona incorrecta, etc
public class PagoInvalidoException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
