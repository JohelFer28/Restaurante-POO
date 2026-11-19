package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
import restaurante.excepciones.OpcionNoEncontradaException;
import restaurante.excepciones.PagoInvalidoException;
import restaurante.excepciones.PedidoNoPermitidoException;
import restaurante.excepciones.SaldoPendienteException;
import restaurante.excepciones.TransicionInvalidaException;

import java.util.ArrayList;
import java.util.List;

// punto de entrada del dominio: tiene las mesas, el menu y el historial de visitas cerradas
// la interfaz (o los servicios) hablan con esta clase y no con cada mesa por separado
public class Restaurante {
    private final List<Mesa> mesas = new ArrayList<>();
    private final List<Visita> historial = new ArrayList<>();
    private final Menu menu;

    // la cantidad de mesas es configurable
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

    public Mesa obtenerMesa(int numero) throws MesaNoDisponibleException {
        for (Mesa m : mesas) {
            if (m.getNumero() == numero) return m;
        }
        throw new MesaNoDisponibleException("La mesa " + numero + " no existe.");
    }

    public Visita abrirVisita(int numeroMesa, Mesero mesero, List<String> aliases)
            throws MesaNoDisponibleException, DatoInvalidoException {
        return obtenerMesa(numeroMesa).abrirVisita(mesero, aliases);
    }

    // el pedido se hace con el id de la opcion, asi la GUI solo necesita el numero que ve en pantalla
    public LineaPedido agregarPedido(int numeroMesa, String alias, int idOpcion, int cantidad)
            throws MesaNoDisponibleException, OpcionNoEncontradaException,
                   PedidoNoPermitidoException, DatoInvalidoException {
        Mesa mesa = obtenerMesa(numeroMesa);
        OpcionMenu opcion = menu.obtener(idOpcion);
        return mesa.agregarPedido(alias, opcion, cantidad);
    }

    public void anularPedido(int numeroMesa, int idLinea, String motivo)
            throws MesaNoDisponibleException, PedidoNoPermitidoException, DatoInvalidoException {
        obtenerMesa(numeroMesa).anularPedido(idLinea, motivo);
    }

    public void solicitarCuenta(int numeroMesa, ModalidadCuenta modalidad)
            throws MesaNoDisponibleException, TransicionInvalidaException, DatoInvalidoException {
        obtenerMesa(numeroMesa).solicitarCuenta(modalidad);
    }

    public Pago registrarPago(int numeroMesa, String alias, int monto, MetodoPago metodo)
            throws MesaNoDisponibleException, TransicionInvalidaException, PagoInvalidoException {
        return obtenerMesa(numeroMesa).registrarPago(alias, monto, metodo);
    }

    // al cerrar, la visita (con sus pedidos y pagos) pasa al historial para las estadisticas
    public Visita cerrarVisita(int numeroMesa)
            throws MesaNoDisponibleException, TransicionInvalidaException, SaldoPendienteException {
        Visita cerrada = obtenerMesa(numeroMesa).cerrarVisita();
        historial.add(cerrada);
        return cerrada;
    }

    public List<Mesa> getMesas() { return List.copyOf(mesas); }
    public List<Visita> getHistorial() { return List.copyOf(historial); }
    public Menu getMenu() { return menu; }
}
