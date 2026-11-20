package restaurante.dominio;

public class PagoEfectivo implements MetodoPago {

    @Override
    public String getNombre() { return "Efectivo"; }

    @Override
    public String generarReferencia(int numeroMesa, int consecutivo) {
        return String.format("EFE-M%d-%04d", numeroMesa, consecutivo);
    }
}
