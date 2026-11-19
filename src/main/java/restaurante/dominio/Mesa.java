package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.MesaNoDisponibleException;
import restaurante.excepciones.TransicionInvalidaException;

import java.util.List;

// el contexto del patron State: la mesa no decide nada, le pasa la operacion a su estado
// el estado nunca se asigna desde afuera, solo cambia con abrirVisita, solicitarCuenta y cerrarVisita
public class Mesa {
    private final int numero;
    private EstadoMesa estado = new EstadoDisponible();
    private Visita visitaActual;   // null cuando la mesa esta disponible

    // sin public: solo Restaurante crea mesas
    Mesa(int numero) {
        this.numero = numero;
    }

    public Visita abrirVisita(Mesero mesero, List<String> aliases)
            throws MesaNoDisponibleException, DatoInvalidoException {
        return estado.abrir(this, mesero, aliases);
    }

    public void solicitarCuenta() throws TransicionInvalidaException {
        estado.solicitarCuenta(this);
    }

    public Visita cerrarVisita() throws TransicionInvalidaException {
        return estado.cerrar(this);
    }

    public int getNumero() { return numero; }
    public String getNombreEstado() { return estado.getNombre(); }
    public boolean estaDisponible() { return estado instanceof EstadoDisponible; }
    public Visita getVisitaActual() { return visitaActual; }

    // metodos que usan los estados (mismo paquete), la GUI no los ve
    void cambiarEstado(EstadoMesa nuevo) { this.estado = nuevo; }
    void asignarVisita(Visita visita) { this.visitaActual = visita; }
    void limpiarVisita() { this.visitaActual = null; }

    @Override
    public String toString() {
        String texto = "Mesa " + numero + ": " + estado.getNombre();
        if (visitaActual != null) {
            texto += " (mesero " + visitaActual.getMesero() + ", " + visitaActual.getComensales().size() + " personas)";
        }
        return texto;
    }
}
