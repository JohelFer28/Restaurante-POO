package restaurante.dominio;

import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Texto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// una atencion de una mesa, desde que se abre hasta que se cierra
public class Visita {
    private final int numeroMesa;
    private final Mesero mesero;
    private final LocalDateTime inicio;
    private LocalDateTime cierre;   // null mientras la visita sigue abierta
    private final List<Comensal> comensales = new ArrayList<>();

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

    // por ahora solo datos basicos, despues se agregan pedidos, subtotales y pagos
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
        return sb.toString();
    }
}
