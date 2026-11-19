package restaurante.recomendacion;

import java.util.List;

// lo que recibe la interfaz: las combinaciones y un mensaje que explica el resultado
// cuando no hay combinaciones, el mensaje dice por que y que puede cambiar el cliente
public class ResultadoRecomendacion {
    private final List<Combinacion> combinaciones;
    private final String mensaje;

    ResultadoRecomendacion(List<Combinacion> combinaciones, String mensaje) {
        this.combinaciones = List.copyOf(combinaciones);
        this.mensaje = mensaje;
    }

    public List<Combinacion> getCombinaciones() { return combinaciones; }
    public String getMensaje() { return mensaje; }
    public boolean hayResultados() { return !combinaciones.isEmpty(); }
}
