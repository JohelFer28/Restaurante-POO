package restaurante.excepciones;

// se intenta abrir una visita en una mesa ocupada o que no existe
// la lanzan Restaurante (mesa inexistente) y el estado de la mesa (mesa ocupada)
public class MesaNoDisponibleException extends RestauranteException {
    private static final long serialVersionUID = 1L;

    public MesaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
