package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;

// mesa con visita abierta: se puede pedir, anular pedidos y solicitar la cuenta
final class EstadoOcupada extends EstadoMesa {

    @Override
    public String getNombre() { return "Ocupada"; }

    // aqui si se permite, por eso no lanza nada
    @Override
    void validarPedido(Mesa mesa) { }

    @Override
    void solicitarCuenta(Mesa mesa, ModalidadCuenta modalidad) throws DatoInvalidoException {
        // se valida antes de cambiar nada
        if (modalidad == null) {
            throw new DatoInvalidoException("Debe elegir la modalidad de la cuenta.");
        }
        mesa.getVisitaActual().definirModalidad(modalidad);
        mesa.cambiarEstado(new EstadoPendientePago());
    }
}
