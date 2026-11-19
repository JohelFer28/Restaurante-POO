package restaurante.dominio;

import restaurante.excepciones.PagoInvalidoException;
import restaurante.excepciones.SaldoPendienteException;

// ya se pidio la cuenta: ya no se pide mas, solo se pagan los saldos y se cierra
// la mesa sigue ocupada hasta que se cierre la visita
final class EstadoPendientePago extends EstadoMesa {

    @Override
    public String getNombre() { return "Pendiente de pago"; }

    @Override
    Pago registrarPago(Mesa mesa, String alias, int monto, MetodoPago metodo) throws PagoInvalidoException {
        return mesa.getVisitaActual().registrarPago(alias, monto, metodo);
    }

    @Override
    Visita cerrar(Mesa mesa) throws SaldoPendienteException {
        Visita visita = mesa.getVisitaActual();
        // solo se cierra con el saldo en cero
        if (visita.getSaldoPendiente() > 0) {
            throw new SaldoPendienteException(visita.getSaldoPendiente());
        }
        visita.cerrar();
        mesa.limpiarVisita();
        mesa.cambiarEstado(new EstadoDisponible());
        return visita;
    }
}
