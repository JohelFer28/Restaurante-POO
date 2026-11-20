package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
import restaurante.excepciones.PagoInvalidoException;
import restaurante.excepciones.PedidoNoPermitidoException;
import restaurante.excepciones.SaldoPendienteException;
import restaurante.excepciones.TransicionInvalidaException;

import java.util.List;

// patron State: cada estado de la mesa es una clase que decide que operaciones permite
// es abstracta porque todas las operaciones traen un comportamiento por defecto (rechazar)
// y cada estado concreto solo sobreescribe las que si le corresponden
public abstract class EstadoMesa {

    public abstract String getNombre();

    // solo EstadoDisponible lo sobreescribe
    Visita abrir(Mesa mesa, Mesero mesero, List<String> aliases)
            throws MesaNoDisponibleException, DatoInvalidoException {
        throw new MesaNoDisponibleException(
                "La mesa " + mesa.getNumero() + " no está disponible, está " + getNombre().toLowerCase() + ".");
    }

    // pedir y anular solo se permite en EstadoOcupada, que sobreescribe este metodo sin hacer nada
    void validarPedido(Mesa mesa) throws PedidoNoPermitidoException {
        throw new PedidoNoPermitidoException(
                "No se pueden registrar ni anular pedidos en la mesa " + mesa.getNumero()
                        + " porque está " + getNombre().toLowerCase() + ".");
    }

    // solo EstadoOcupada lo sobreescribe
    void solicitarCuenta(Mesa mesa, ModalidadCuenta modalidad)
            throws TransicionInvalidaException, DatoInvalidoException {
        throw rechazar(mesa, "solicitar la cuenta");
    }

    // solo EstadoPendientePago lo sobreescribe
    Pago registrarPago(Mesa mesa, String alias, int monto, MetodoPago metodo)
            throws TransicionInvalidaException, PagoInvalidoException {
        throw rechazar(mesa, "registrar un pago");
    }

    // solo EstadoPendientePago lo sobreescribe
    Visita cerrar(Mesa mesa) throws TransicionInvalidaException, SaldoPendienteException {
        throw rechazar(mesa, "cerrar la visita");
    }

    private TransicionInvalidaException rechazar(Mesa mesa, String operacion) {
        return new TransicionInvalidaException(
                "No se puede " + operacion + " de la mesa " + mesa.getNumero()
                        + " porque está " + getNombre().toLowerCase() + ".");
    }
}
