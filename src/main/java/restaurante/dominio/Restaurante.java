package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
import restaurante.excepciones.OpcionNoEncontradaException;
import restaurante.excepciones.PagoInvalidoException;
import restaurante.excepciones.PedidoNoPermitidoException;
import restaurante.excepciones.SaldoPendienteException;
import restaurante.excepciones.TransicionInvalidaException;
<<<<<<< HEAD
import restaurante.observador.EventoRestaurante;
import restaurante.observador.ObservadorRestaurante;
import restaurante.observador.TipoEventoRestaurante;
import restaurante.util.Formato;
=======
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
// punto de entrada del dominio y Sujeto Observable (patron Observer):
// notifica cambios relevantes a las vistas y bitacoras sin acoplarse a Swing
public class Restaurante {
    private final List<Mesa> mesas = new ArrayList<>();
    private final List<Visita> historial = new ArrayList<>();
    private final List<ObservadorRestaurante> observadores = new ArrayList<>();
    private final Menu menu;

=======
// punto de entrada del dominio: tiene las mesas, el menu y el historial de visitas cerradas
// la interfaz (o los servicios) hablan con esta clase y no con cada mesa por separado
public class Restaurante {
    private final List<Mesa> mesas = new ArrayList<>();
    private final List<Visita> historial = new ArrayList<>();
    private final Menu menu;

    // la cantidad de mesas es configurable
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    public Restaurante(int cantidadMesas, Menu menu) throws DatoInvalidoException {
        if (cantidadMesas < 1) {
            throw new DatoInvalidoException("El restaurante debe tener al menos una mesa.");
        }
        if (menu == null) {
            throw new DatoInvalidoException("El restaurante necesita un menú.");
        }
        for (int i = 1; i <= cantidadMesas; i++) {
            mesas.add(new Mesa(i));
        }
        this.menu = menu;
    }

<<<<<<< HEAD
    // gestion de observadores (Patron Observer)

    public void agregarObservador(ObservadorRestaurante observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(ObservadorRestaurante observador) {
        observadores.remove(observador);
    }

    private void notificar(TipoEventoRestaurante tipo, int numeroMesa, String descripcion) {
        EventoRestaurante evento = new EventoRestaurante(tipo, numeroMesa, descripcion);
        for (ObservadorRestaurante obs : List.copyOf(observadores)) {
            obs.alOcurrirEvento(evento);
        }
    }

    public void notificarCambioMenu(String detalle) {
        notificar(TipoEventoRestaurante.MENU_ACTUALIZADO, 0, detalle);
    }

    // operaciones del dominio

=======
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    public Mesa obtenerMesa(int numero) throws MesaNoDisponibleException {
        for (Mesa m : mesas) {
            if (m.getNumero() == numero) return m;
        }
        throw new MesaNoDisponibleException("La mesa " + numero + " no existe.");
    }

    public Visita abrirVisita(int numeroMesa, Mesero mesero, List<String> aliases)
            throws MesaNoDisponibleException, DatoInvalidoException {
<<<<<<< HEAD
        Visita visita = obtenerMesa(numeroMesa).abrirVisita(mesero, aliases);
        notificar(TipoEventoRestaurante.VISITA_ABIERTA, numeroMesa,
                "Visita abierta en Mesa " + numeroMesa + " (Mesero: " + mesero + ", "
                        + visita.getComensales().size() + " comensales).");
        return visita;
    }

=======
        return obtenerMesa(numeroMesa).abrirVisita(mesero, aliases);
    }

    // el pedido se hace con el id de la opcion, asi la GUI solo necesita el numero que ve en pantalla
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    public LineaPedido agregarPedido(int numeroMesa, String alias, int idOpcion, int cantidad)
            throws MesaNoDisponibleException, OpcionNoEncontradaException,
                   PedidoNoPermitidoException, DatoInvalidoException {
        Mesa mesa = obtenerMesa(numeroMesa);
        OpcionMenu opcion = menu.obtener(idOpcion);
<<<<<<< HEAD
        LineaPedido linea = mesa.agregarPedido(alias, opcion, cantidad);
        notificar(TipoEventoRestaurante.PEDIDO_REGISTRADO, numeroMesa,
                "Mesa " + numeroMesa + " | " + linea.getComensal() + " pidió "
                        + cantidad + "x " + opcion.getNombre() + " (" + Formato.colones(linea.getSubtotal()) + ").");
        return linea;
=======
        return mesa.agregarPedido(alias, opcion, cantidad);
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    }

    public void anularPedido(int numeroMesa, int idLinea, String motivo)
            throws MesaNoDisponibleException, PedidoNoPermitidoException, DatoInvalidoException {
        obtenerMesa(numeroMesa).anularPedido(idLinea, motivo);
<<<<<<< HEAD
        notificar(TipoEventoRestaurante.PEDIDO_ANULADO, numeroMesa,
                "Mesa " + numeroMesa + " | Pedido #" + idLinea + " anulado (" + motivo.trim() + ").");
=======
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    }

    public void solicitarCuenta(int numeroMesa, ModalidadCuenta modalidad)
            throws MesaNoDisponibleException, TransicionInvalidaException, DatoInvalidoException {
        obtenerMesa(numeroMesa).solicitarCuenta(modalidad);
<<<<<<< HEAD
        notificar(TipoEventoRestaurante.CUENTA_SOLICITADA, numeroMesa,
                "Mesa " + numeroMesa + " | Cuenta solicitada en modalidad: " + modalidad + ".");
=======
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    }

    public Pago registrarPago(int numeroMesa, String alias, int monto, MetodoPago metodo)
            throws MesaNoDisponibleException, TransicionInvalidaException, PagoInvalidoException {
<<<<<<< HEAD
        Pago pago = obtenerMesa(numeroMesa).registrarPago(alias, monto, metodo);
        String quien = pago.getAliasComensal() == null ? "cuenta conjunta" : pago.getAliasComensal();
        notificar(TipoEventoRestaurante.PAGO_REGISTRADO, numeroMesa,
                "Mesa " + numeroMesa + " | Pago de " + Formato.colones(monto)
                        + " (" + quien + ", " + metodo.getNombre() + ", ref " + pago.getReferencia() + ").");
        return pago;
    }

=======
        return obtenerMesa(numeroMesa).registrarPago(alias, monto, metodo);
    }

    // al cerrar, la visita (con sus pedidos y pagos) pasa al historial para las estadisticas
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
    public Visita cerrarVisita(int numeroMesa)
            throws MesaNoDisponibleException, TransicionInvalidaException, SaldoPendienteException {
        Visita cerrada = obtenerMesa(numeroMesa).cerrarVisita();
        historial.add(cerrada);
<<<<<<< HEAD
        notificar(TipoEventoRestaurante.VISITA_CERRADA, numeroMesa,
                "Mesa " + numeroMesa + " | Visita cerrada y mesa liberada. Total cobrado: "
                        + Formato.colones(cerrada.totalMesa()) + ".");
=======
>>>>>>> 5d5290bbb7032902a2600d8a5ac7821032c2fa98
        return cerrada;
    }

    public List<Mesa> getMesas() { return List.copyOf(mesas); }
    public List<Visita> getHistorial() { return List.copyOf(historial); }
    public Menu getMenu() { return menu; }
}
