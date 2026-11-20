package restaurante.excepciones;

// se intenta pedir o anular cuando la mesa no esta en un estado que lo permita
// ejemplo: la visita ya se cerro o ya se pidio la cuenta
public class PedidoNoPermitidoException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public PedidoNoPermitidoException(String mensaje) {
        super(mensaje);
    }
}
