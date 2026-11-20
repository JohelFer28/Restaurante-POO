package restaurante.observador;

public enum TipoEventoRestaurante {
    VISITA_ABIERTA("Visita abierta"),
    PEDIDO_REGISTRADO("Pedido registrado"),
    PEDIDO_ANULADO("Pedido anulado"),
    CUENTA_SOLICITADA("Cuenta solicitada"),
    PAGO_REGISTRADO("Pago registrado"),
    VISITA_CERRADA("Visita cerrada"),
    MENU_ACTUALIZADO("Menú actualizado");

    private final String etiqueta;

    TipoEventoRestaurante(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
