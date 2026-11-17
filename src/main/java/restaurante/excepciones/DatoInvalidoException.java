package restaurante.excepciones;

// dato que no cumple una regla (precio negativo, nombre vacio, rango invertido, etc)
// la lanza el objeto del dominio y la interfaz muestra el mensaje
public class DatoInvalidoException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
