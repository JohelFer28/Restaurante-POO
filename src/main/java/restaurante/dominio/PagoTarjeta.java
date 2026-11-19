package restaurante.dominio;

// pago ficticio, no se conecta con ningun banco
public class PagoTarjeta implements MetodoPago {

    @Override
    public String getNombre() { return "Tarjeta"; }

    @Override
    public String generarReferencia(int numeroMesa, int consecutivo) {
        return String.format("TRJ-M%d-%04d", numeroMesa, consecutivo);
    }
}
