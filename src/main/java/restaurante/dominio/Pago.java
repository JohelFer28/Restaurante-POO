package restaurante.dominio;

import restaurante.util.Formato;

import java.time.LocalDateTime;

// un pago registrado en una visita, no se puede modificar una vez creado
public class Pago {
    private final int id;
    private final String aliasComensal;   // null cuando es cuenta conjunta
    private final int monto;
    private final MetodoPago metodo;
    private final String referencia;
    private final LocalDateTime fecha;

    // sin public: solo Visita crea pagos, despues de validarlos
    Pago(int id, String aliasComensal, int monto, MetodoPago metodo, String referencia) {
        this.id = id;
        this.aliasComensal = aliasComensal;
        this.monto = monto;
        this.metodo = metodo;
        this.referencia = referencia;
        this.fecha = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getAliasComensal() { return aliasComensal; }
    public int getMonto() { return monto; }
    public MetodoPago getMetodo() { return metodo; }
    public String getReferencia() { return referencia; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        String quien = aliasComensal == null ? "mesa" : aliasComensal;
        return "Pago " + id + " (" + quien + "): " + Formato.colones(monto) + " por "
                + metodo.getNombre() + ", ref " + referencia;
    }
}
