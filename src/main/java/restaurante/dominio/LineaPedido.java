package restaurante.dominio;

import restaurante.util.Formato;

import java.time.LocalDateTime;

// un renglon de pedido: quien lo pidio, que pidio, cuantos y a que precio
// el precio se copia al pedir, asi un cambio de precio en el menu no afecta pedidos anteriores
// las anulaciones no se borran, quedan marcadas con fecha y motivo
public class LineaPedido {
    private final int id;
    private final Comensal comensal;
    private final OpcionMenu opcion;
    private final int cantidad;
    private final int precioUnitario;
    private final LocalDateTime fecha;
    private boolean anulada = false;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;

    // sin public: solo Visita crea lineas, despues de validar
    LineaPedido(int id, Comensal comensal, OpcionMenu opcion, int cantidad) {
        this.id = id;
        this.comensal = comensal;
        this.opcion = opcion;
        this.cantidad = cantidad;
        this.precioUnitario = opcion.getPrecio();
        this.fecha = LocalDateTime.now();
    }

    void anular(String motivo) {
        this.anulada = true;
        this.fechaAnulacion = LocalDateTime.now();
        this.motivoAnulacion = motivo.trim();
    }

    public int getSubtotal() { return precioUnitario * cantidad; }

    public int getId() { return id; }
    public Comensal getComensal() { return comensal; }
    public OpcionMenu getOpcion() { return opcion; }
    public Categoria getCategoria() { return opcion.getCategoria(); }
    public int getCantidad() { return cantidad; }
    public int getPrecioUnitario() { return precioUnitario; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean estaAnulada() { return anulada; }
    public LocalDateTime getFechaAnulacion() { return fechaAnulacion; }
    public String getMotivoAnulacion() { return motivoAnulacion; }

    @Override
    public String toString() {
        String texto = "#" + id + " " + comensal + ": " + cantidad + " x " + opcion.getNombre()
                + " = " + Formato.colones(getSubtotal());
        if (anulada) {
            texto += " (ANULADO: " + motivoAnulacion + ")";
        }
        return texto;
    }
}
