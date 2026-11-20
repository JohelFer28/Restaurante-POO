package restaurante.observador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EventoRestaurante {
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TipoEventoRestaurante tipo;
    private final int numeroMesa;
    private final String descripcion;
    private final LocalDateTime fechaHora;

    public EventoRestaurante(TipoEventoRestaurante tipo, int numeroMesa, String descripcion) {
        this.tipo = tipo;
        this.numeroMesa = numeroMesa;
        this.descripcion = descripcion;
        this.fechaHora = LocalDateTime.now();
    }

    public TipoEventoRestaurante getTipo() { return tipo; }
    public int getNumeroMesa() { return numeroMesa; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }

    @Override
    public String toString() {
        return "[" + fechaHora.format(FORMATO_HORA) + "] [" + tipo.getEtiqueta() + "] " + descripcion;
    }
}
