package restaurante.observador;

// interfaz del patron Observer: cualquier vista, bitacora o servicio que quiera reaccionar
// a cambios en el restaurante la implementa sin que el dominio conozca sus detalles
public interface ObservadorRestaurante {
    void alOcurrirEvento(EventoRestaurante evento);
}
