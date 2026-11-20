package restaurante.observador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// observador concreto que guarda el historial cronologico de eventos para auditoria
public class BitacoraEventos implements ObservadorRestaurante {
    private final List<EventoRestaurante> eventos = new ArrayList<>();

    @Override
    public void alOcurrirEvento(EventoRestaurante evento) {
        if (evento != null) {
            eventos.add(evento);
        }
    }

    // copia de solo lectura para que nadie borre registros de auditoria desde afuera
    public List<EventoRestaurante> getEventos() {
        return Collections.unmodifiableList(new ArrayList<>(eventos));
    }

    public void limpiar() {
        eventos.clear();
    }
}
