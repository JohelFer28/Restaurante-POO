package restaurante.observador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// observador concreto que conserva la auditoria cronologica de eventos del sistema
public class BitacoraEventos implements ObservadorRestaurante {
    private final List<EventoRestaurante> eventos = new ArrayList<>();

    @Override
    public void alOcurrirEvento(EventoRestaurante evento) {
        if (evento != null) {
            eventos.add(evento);
        }
    }

    public List<EventoRestaurante> getEventos() {
        return Collections.unmodifiableList(new ArrayList<>(eventos));
    }

    public void limpiar() {
        eventos.clear();
    }
}
