package restaurante.dominio;

// forma en que se paga la cuenta de la mesa, se elige al solicitar la cuenta
public enum ModalidadCuenta {
    CONJUNTA("Cuenta conjunta"),
    INDIVIDUAL("Pagos por persona");

    private final String descripcion;

    ModalidadCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return descripcion; }
}
