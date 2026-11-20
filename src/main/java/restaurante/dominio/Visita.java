package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.PagoInvalidoException;
import restaurante.util.Formato;
import restaurante.util.Texto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// una atencion de una mesa, desde que se abre hasta que se cierra
// guarda comensales, pedidos y pagos, y con eso calcula subtotales y saldo
// el dinero siempre son enteros en colones
public class Visita {
    public static final int CANTIDAD_MAXIMA = 99;   // por renglon de pedido

    private final int numeroMesa;
    private final Mesero mesero;
    private final LocalDateTime inicio;
    private LocalDateTime cierre;   // null mientras la visita sigue abierta
    private final List<Comensal> comensales = new ArrayList<>();
    private final List<LineaPedido> lineas = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private ModalidadCuenta modalidad;   // null hasta que se solicita la cuenta
    private int siguienteIdLinea = 1;
    private int siguienteIdPago = 1;

    // sin public: las visitas las crea la mesa al abrirse
    // valida todo en el constructor, si algo falla la visita nunca llega a existir
    Visita(int numeroMesa, Mesero mesero, List<String> aliases) throws DatoInvalidoException {
        if (mesero == null) {
            throw new DatoInvalidoException("Debe asignar un mesero responsable.");
        }
        if (aliases == null || aliases.isEmpty()) {
            throw new DatoInvalidoException("La visita debe tener al menos un comensal.");
        }
        Set<String> vistos = new HashSet<>();
        List<Comensal> nuevos = new ArrayList<>();
        for (String alias : aliases) {
            if (alias == null || alias.isBlank()) {
                throw new DatoInvalidoException("El alias de un comensal no puede estar vacío.");
            }
            // normalizar para que "Ana" y "ana" cuenten como el mismo alias
            if (!vistos.add(Texto.normalizar(alias))) {
                throw new DatoInvalidoException("El alias \"" + alias.trim() + "\" está repetido en la visita.");
            }
            nuevos.add(new Comensal(alias.trim()));
        }
        this.numeroMesa = numeroMesa;
        this.mesero = mesero;
        this.inicio = LocalDateTime.now();
        this.comensales.addAll(nuevos);
    }

    // pedidos

    // valida todo antes de crear la linea, si algo falla no queda nada a medias
    LineaPedido agregarPedido(String alias, OpcionMenu opcion, int cantidad) throws DatoInvalidoException {
        Comensal comensal = buscarComensal(alias);
        if (opcion == null) {
            throw new DatoInvalidoException("Debe elegir una opción del menú.");
        }
        if (!opcion.estaDisponible()) {
            throw new DatoInvalidoException("\"" + opcion.getNombre() + "\" no está disponible.");
        }
        if (!opcion.getCategoria().estaActiva()) {
            throw new DatoInvalidoException("La categoría \"" + opcion.getCategoria() + "\" está desactivada.");
        }
        if (cantidad < 1 || cantidad > CANTIDAD_MAXIMA) {
            throw new DatoInvalidoException("La cantidad debe estar entre 1 y " + CANTIDAD_MAXIMA + ".");
        }
        LineaPedido linea = new LineaPedido(siguienteIdLinea, comensal, opcion, cantidad);
        siguienteIdLinea++;
        lineas.add(linea);
        opcion.marcarComoUsada();   // desde ahora la opcion ya no se puede eliminar del menu
        return linea;
    }

    // la linea no se borra, queda marcada como anulada con su motivo
    void anularPedido(int idLinea, String motivo) throws DatoInvalidoException {
        if (motivo == null || motivo.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el motivo de la anulación.");
        }
        for (LineaPedido l : lineas) {
            if (l.getId() == idLinea) {
                if (l.estaAnulada()) {
                    throw new DatoInvalidoException("El pedido #" + idLinea + " ya está anulado.");
                }
                l.anular(motivo);
                return;
            }
        }
        throw new DatoInvalidoException("No existe el pedido #" + idLinea + " en esta visita.");
    }

    // todos los pedidos, incluidos los anulados (es la bitacora)
    public List<LineaPedido> getPedidos() { return List.copyOf(lineas); }

    public List<LineaPedido> getPedidosActivos() {
        List<LineaPedido> activos = new ArrayList<>();
        for (LineaPedido l : lineas) {
            if (!l.estaAnulada()) activos.add(l);
        }
        return activos;
    }

    // subtotales

    // los pedidos anulados no cuentan en ningun calculo
    public int subtotalComensal(String alias) throws DatoInvalidoException {
        return subtotalDe(buscarComensal(alias));
    }

    public int totalMesa() {
        int total = 0;
        for (LineaPedido l : lineas) {
            if (!l.estaAnulada()) total += l.getSubtotal();
        }
        return total;
    }

    // clave: nombre de la categoria
    public Map<String, Integer> subtotalPorCategoria() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (LineaPedido l : lineas) {
            if (l.estaAnulada()) continue;
            String clave = l.getCategoria().getNombre();
            resultado.put(clave, resultado.getOrDefault(clave, 0) + l.getSubtotal());
        }
        return resultado;
    }

    // clave: nombre del articulo
    public Map<String, Integer> subtotalPorArticulo() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (LineaPedido l : lineas) {
            if (l.estaAnulada()) continue;
            String clave = l.getOpcion().getNombre();
            resultado.put(clave, resultado.getOrDefault(clave, 0) + l.getSubtotal());
        }
        return resultado;
    }

    private int subtotalDe(Comensal comensal) {
        int suma = 0;
        for (LineaPedido l : lineas) {
            if (!l.estaAnulada() && l.getComensal() == comensal) suma += l.getSubtotal();
        }
        return suma;
    }

    // cuenta y pagos

    void definirModalidad(ModalidadCuenta modalidad) {
        this.modalidad = modalidad;
    }

    // todas las reglas se validan antes de crear el pago, si algo falla el saldo no cambia
    Pago registrarPago(String alias, int monto, MetodoPago metodo) throws PagoInvalidoException {
        if (metodo == null) {
            throw new PagoInvalidoException("Debe elegir un método de pago.");
        }
        if (monto <= 0) {
            throw new PagoInvalidoException("El monto del pago debe ser positivo.");
        }
        String aliasPagador = null;
        if (modalidad == ModalidadCuenta.CONJUNTA) {
            if (alias != null && !alias.isBlank()) {
                throw new PagoInvalidoException("En cuenta conjunta el pago no se asigna a una persona.");
            }
            if (monto > getSaldoPendiente()) {
                throw new PagoInvalidoException("El monto excede el saldo pendiente de "
                        + Formato.colones(getSaldoPendiente()) + ".");
            }
        } else {
            Comensal comensal;
            try {
                comensal = buscarComensal(alias);
            } catch (DatoInvalidoException e) {
                throw new PagoInvalidoException("Debe indicar qué comensal paga. " + e.getMessage());
            }
            int saldo = saldoDe(comensal);
            if (monto > saldo) {
                throw new PagoInvalidoException("El monto excede lo que debe " + comensal + ": "
                        + Formato.colones(saldo) + ".");
            }
            aliasPagador = comensal.getAlias();
        }
        Pago pago = new Pago(siguienteIdPago, aliasPagador, monto, metodo,
                metodo.generarReferencia(numeroMesa, siguienteIdPago));
        siguienteIdPago++;
        pagos.add(pago);
        return pago;
    }

    public ModalidadCuenta getModalidad() { return modalidad; }

    public List<Pago> getPagos() { return List.copyOf(pagos); }

    public int totalPagado() {
        int total = 0;
        for (Pago p : pagos) total += p.getMonto();
        return total;
    }

    public int getSaldoPendiente() { return totalMesa() - totalPagado(); }

    // lo que le falta pagar a una persona (se usa en pagos por persona)
    public int saldoComensal(String alias) throws DatoInvalidoException {
        return saldoDe(buscarComensal(alias));
    }

    private int saldoDe(Comensal comensal) {
        int pagado = 0;
        for (Pago p : pagos) {
            if (comensal.getAlias().equals(p.getAliasComensal())) pagado += p.getMonto();
        }
        return subtotalDe(comensal) - pagado;
    }

    // comensales y datos generales

    private Comensal buscarComensal(String alias) throws DatoInvalidoException {
        if (alias == null || alias.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el comensal.");
        }
        String buscado = Texto.normalizar(alias);
        for (Comensal c : comensales) {
            if (Texto.normalizar(c.getAlias()).equals(buscado)) return c;
        }
        throw new DatoInvalidoException("No existe el comensal \"" + alias.trim() + "\" en esta visita.");
    }

    // lo llama el estado de la mesa al cerrar
    void cerrar() {
        this.cierre = LocalDateTime.now();
    }

    public int getNumeroMesa() { return numeroMesa; }
    public Mesero getMesero() { return mesero; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getCierre() { return cierre; }
    public boolean estaAbierta() { return cierre == null; }

    // copia, para que nadie agregue comensales desde afuera
    public List<Comensal> getComensales() { return List.copyOf(comensales); }

    public String resumen() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("Mesa ").append(numeroMesa)
          .append(" | Mesero: ").append(mesero)
          .append(" | Inicio: ").append(inicio.format(f));
        if (cierre != null) {
            sb.append(" | Cierre: ").append(cierre.format(f));
        }
        sb.append("\nComensales (").append(comensales.size()).append("): ");
        for (int i = 0; i < comensales.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(comensales.get(i));
        }

        sb.append("\nPedidos:");
        if (lineas.isEmpty()) sb.append(" ninguno");
        for (LineaPedido l : lineas) {
            sb.append("\n  ").append(l);
        }

        sb.append("\nSubtotal por persona:");
        for (Comensal c : comensales) {
            sb.append("\n  ").append(c).append(": ").append(Formato.colones(subtotalDe(c)));
        }
        sb.append("\nTotal de la mesa: ").append(Formato.colones(totalMesa()));

        if (modalidad != null) {
            sb.append("\nModalidad: ").append(modalidad);
        }
        sb.append("\nPagos:");
        if (pagos.isEmpty()) sb.append(" ninguno");
        for (Pago p : pagos) {
            sb.append("\n  ").append(p);
        }
        sb.append("\nSaldo pendiente: ").append(Formato.colones(getSaldoPendiente()));
        return sb.toString();
    }
}
