package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
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

    public void solicitarCuenta(int numeroMesa)
            throws MesaNoDisponibleException, TransicionInvalidaException {
        obtenerMesa(numeroMesa).solicitarCuenta();
    }

    // al cerrar, la visita pasa al historial (lo van a usar las estadisticas)
    public Visita cerrarVisita(int numeroMesa)
            throws MesaNoDisponibleException, TransicionInvalidaException {
        Visita cerrada = obtenerMesa(numeroMesa).cerrarVisita();
        historial.add(cerrada);
        return cerrada;
    }

    public List<Mesa> getMesas() { return List.copyOf(mesas); }
    public List<Visita> getHistorial() { return List.copyOf(historial); }
    public Menu getMenu() { return menu; }
}
