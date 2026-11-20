package restaurante.excepciones;

import restaurante.util.Formato;

// se intenta cerrar una visita que todavia tiene saldo por pagar
public class SaldoPendienteException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public SaldoPendienteException(int saldo) {
        super("No se puede cerrar la visita, aún falta pagar " + Formato.colones(saldo) + ".");
    }
}
