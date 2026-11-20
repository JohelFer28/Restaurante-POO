package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;

import java.util.List;

// mesa libre: lo unico permitido es abrir una visita
final class EstadoDisponible extends EstadoMesa {

    @Override
    public String getNombre() { return "Disponible"; }

    @Override
    Visita abrir(Mesa mesa, Mesero mesero, List<String> aliases) throws DatoInvalidoException {
        // primero se crea la visita: si algun dato es invalido lanza excepcion
        // y la mesa todavia no se ha tocado
        Visita visita = new Visita(mesa.getNumero(), mesero, aliases);
        mesa.asignarVisita(visita);
        mesa.cambiarEstado(new EstadoOcupada());
        return visita;
    }
}
