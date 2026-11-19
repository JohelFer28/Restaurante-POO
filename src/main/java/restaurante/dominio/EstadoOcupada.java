package restaurante.dominio;

import restaurante.excepciones.TransicionInvalidaException;

// mesa con visita abierta: se pueden hacer pedidos (parte 3) y pedir la cuenta
final class EstadoOcupada extends EstadoMesa {

    @Override
    public String getNombre() { return "Ocupada"; }

    @Override
    void solicitarCuenta(Mesa mesa) throws TransicionInvalidaException {
        mesa.cambiarEstado(new EstadoPendientePago());
    }
}
