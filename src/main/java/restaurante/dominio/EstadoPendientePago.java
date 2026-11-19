package restaurante.dominio;

import restaurante.excepciones.TransicionInvalidaException;

// ya se pidio la cuenta: se pueden registrar pagos (parte 4) y cerrar
// la mesa sigue ocupada hasta que se cierre la visita
final class EstadoPendientePago extends EstadoMesa {

    @Override
    public String getNombre() { return "Pendiente de pago"; }

    @Override
    Visita cerrar(Mesa mesa) throws TransicionInvalidaException {
        // en la parte 4 aqui se va a validar que la cuenta este saldada
        Visita visita = mesa.getVisitaActual();
        visita.cerrar();
        mesa.limpiarVisita();
        mesa.cambiarEstado(new EstadoDisponible());
        return visita;
    }
}
