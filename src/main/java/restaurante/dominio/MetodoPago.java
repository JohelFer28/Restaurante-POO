package restaurante.dominio;

// interfaz: define lo que todo metodo de pago debe saber hacer
// asi Visita registra pagos sin saber si son en efectivo, tarjeta o sinpe
// para agregar otro metodo basta con crear una clase nueva que la implemente
public interface MetodoPago {

    String getNombre();

    // referencia simulada del pago, ejemplo EFE-M3-0002 (mesa 3, pago numero 2)
    String generarReferencia(int numeroMesa, int consecutivo);
}
