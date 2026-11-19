package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
import restaurante.excepciones.TransicionInvalidaException;

import java.util.List;

// patron State: cada estado de la mesa es una clase que decide que operaciones permite
// es abstracta porque las tres operaciones traen un comportamiento por defecto (rechazar)
// y cada estado concreto solo sobreescribe la que si le corresponde
public abstract class EstadoMesa {

    public abstract String getNombre();

    // por defecto no se puede abrir, solo EstadoDisponible lo sobreescribe
    Visita abrir(Mesa mesa, Mesero mesero, List<String> aliases)
            throws MesaNoDisponibleException, DatoInvalidoException {
        throw new MesaNoDisponibleException(
                "La mesa " + mesa.getNumero() + " no está disponible, está " + getNombre().toLowerCase() + ".");
    }

    // por defecto no se puede pedir la cuenta, solo EstadoOcupada lo sobreescribe
    void solicitarCuenta(Mesa mesa) throws TransicionInvalidaException {
        throw rechazar(mesa, "solicitar la cuenta");
    }

    // por defecto no se puede cerrar, solo EstadoPendientePago lo sobreescribe
    Visita cerrar(Mesa mesa) throws TransicionInvalidaException {
        throw rechazar(mesa, "cerrar la visita");
    }

    private TransicionInvalidaException rechazar(Mesa mesa, String operacion) {
        return new TransicionInvalidaException(
                "No se puede " + operacion + " de la mesa " + mesa.getNumero()
                        + " porque está " + getNombre().toLowerCase() + ".");
    }
}
