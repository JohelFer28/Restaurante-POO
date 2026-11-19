package restaurante.recomendacion;

import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// algoritmo simple para comparar: genera TODAS las combinaciones (producto cartesiano)
// y al final se queda con las que no pasan del limite
// sirve de referencia en las pruebas: si la version con poda da lo mismo, esta bien
public class RecomendacionFuerzaBruta implements EstrategiaRecomendacion {

    @Override
    public List<Combinacion> generar(Menu menu, OpcionMenu proteina, List<Categoria> requeridas, int maxCalorias) {
        List<Combinacion> resultado = new ArrayList<>();
        List<List<OpcionMenu>> porCategoria = new ArrayList<>();
        for (Categoria c : requeridas) {
            List<OpcionMenu> opciones = menu.opcionesDisponibles(c);
            if (opciones.isEmpty()) {
                return resultado;
            }
            porCategoria.add(opciones);
        }
        combinar(0, porCategoria, new OpcionMenu[requeridas.size()], proteina, maxCalorias, resultado);
        Collections.sort(resultado);
        return resultado;
    }

    private void combinar(int nivel, List<List<OpcionMenu>> porCategoria, OpcionMenu[] elegidas,
                          OpcionMenu proteina, int maxCalorias, List<Combinacion> resultado) {
        if (nivel == elegidas.length) {
            Combinacion c = new Combinacion(proteina, Arrays.asList(elegidas));
            if (c.getTotalCalorias() <= maxCalorias) {
                resultado.add(c);
            }
            return;
        }
        for (OpcionMenu opcion : porCategoria.get(nivel)) {
            elegidas[nivel] = opcion;
            combinar(nivel + 1, porCategoria, elegidas, proteina, maxCalorias, resultado);
        }
    }
}
