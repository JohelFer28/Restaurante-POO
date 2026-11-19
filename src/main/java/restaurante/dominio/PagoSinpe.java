package restaurante.dominio;

public class PagoSinpe implements MetodoPago {

    @Override
    public String getNombre() { return "SINPE Móvil"; }

    @Override
    public String generarReferencia(int numeroMesa, int consecutivo) {
        return String.format("SIN-M%d-%04d", numeroMesa, consecutivo);
    }
}
