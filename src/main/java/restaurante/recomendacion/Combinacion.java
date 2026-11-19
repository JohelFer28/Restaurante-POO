package restaurante.recomendacion;

import restaurante.dominio.OpcionMenu;
import restaurante.util.Formato;
import restaurante.util.Texto;

import java.util.ArrayList;
import java.util.List;

// una comida completa: la proteina elegida mas una opcion por cada categoria requerida
// no se puede modificar una vez creada
public class Combinacion implements Comparable<Combinacion> {
    private final OpcionMenu proteina;
    private final List<OpcionMenu> opciones;   // en el mismo orden de las categorias requeridas
    private final int totalCalorias;
    private final int totalPrecio;
    private final String claveNombres;   // para ordenar por nombre
    private final String claveIds;       // desempate final, asi el orden nunca depende del azar

    Combinacion(OpcionMenu proteina, List<OpcionMenu> opciones) {
        this.proteina = proteina;
        this.opciones = List.copyOf(opciones);

        int calorias = proteina.getCalorias();
        int precio = proteina.getPrecio();
        StringBuilder nombres = new StringBuilder(Texto.normalizar(proteina.getNombre()));
        StringBuilder ids = new StringBuilder(String.format("%06d", proteina.getId()));
        for (OpcionMenu o : this.opciones) {
            calorias += o.getCalorias();
            precio += o.getPrecio();
            nombres.append("|").append(Texto.normalizar(o.getNombre()));
            ids.append("-").append(String.format("%06d", o.getId()));
        }
        this.totalCalorias = calorias;
        this.totalPrecio = precio;
        this.claveNombres = nombres.toString();
        this.claveIds = ids.toString();
    }

    public OpcionMenu getProteina() { return proteina; }
    public List<OpcionMenu> getOpciones() { return opciones; }
    public int getTotalCalorias() { return totalCalorias; }
    public int getTotalPrecio() { return totalPrecio; }

    // la proteina primero y despues el resto
    public List<OpcionMenu> getTodas() {
        List<OpcionMenu> todas = new ArrayList<>();
        todas.add(proteina);
        todas.addAll(opciones);
        return todas;
    }

    // orden de presentacion: calorias ascendentes, luego nombres, luego ids
    @Override
    public int compareTo(Combinacion otra) {
        if (totalCalorias != otra.totalCalorias) {
            return Integer.compare(totalCalorias, otra.totalCalorias);
        }
        int porNombre = claveNombres.compareTo(otra.claveNombres);
        if (porNombre != 0) return porNombre;
        return claveIds.compareTo(otra.claveIds);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (OpcionMenu o : getTodas()) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(o.getNombre()).append(" (").append(o.getCategoria()).append(")");
        }
        sb.append(" = ").append(totalCalorias).append(" kcal, ").append(Formato.colones(totalPrecio));
        return sb.toString();
    }
}
