package restaurante.recomendacion;

import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// algoritmo principal: backtracking (vuelta atras) con poda
//
// idea: se arma la comida eligiendo una opcion por categoria, una categoria por nivel
// antes de bajar un nivel se calcula lo minimo que todavia faltaria sumar
// si acumulado + minimo que falta ya supera el limite, esa rama no puede dar ninguna solucion y se corta
//
// por que es correcto:
// 1) no se pierde ninguna solucion: solo se cortan ramas cuya suma minima posible ya pasa el limite
// 2) no se acepta ninguna mala: al llegar al final la suma es exacta y es menor o igual al limite
// 3) no hay duplicados: cada categoria tiene su nivel fijo y en cada nivel se elige una sola opcion,
//    entonces cada combinacion se genera por un unico camino
public class RecomendacionPorPoda implements EstrategiaRecomendacion {

    private static final Comparator<OpcionMenu> POR_CALORIAS =
            Comparator.comparingInt(OpcionMenu::getCalorias).thenComparing(OpcionMenu::getNombre);

    @Override
    public List<Combinacion> generar(Menu menu, OpcionMenu proteina, List<Categoria> requeridas, int maxCalorias) {
        List<Combinacion> resultado = new ArrayList<>();
        int n = requeridas.size();

        // paso 1: opciones disponibles de cada categoria, de menos a mas calorias
        List<List<OpcionMenu>> porCategoria = new ArrayList<>();
        for (Categoria c : requeridas) {
            List<OpcionMenu> opciones = new ArrayList<>(menu.opcionesDisponibles(c));
            if (opciones.isEmpty()) {
                return resultado;   // una categoria requerida sin opciones: no hay combinaciones
            }
            opciones.sort(POR_CALORIAS);
            porCategoria.add(opciones);
        }

        // paso 2: explorar primero las categorias con menos opciones (optimizacion opcional)
        Integer[] orden = new Integer[n];
        for (int i = 0; i < n; i++) orden[i] = i;
        Arrays.sort(orden, Comparator.comparingInt(i -> porCategoria.get(i).size()));

        // paso 3: minimoDesde[i] = calorias minimas que se sumarian desde el nivel i hasta el final
        int[] minimoDesde = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            minimoDesde[i] = minimoDesde[i + 1] + porCategoria.get(orden[i]).get(0).getCalorias();
        }

        // si ni la mejor comida posible cabe, no hay nada que explorar
        if (proteina.getCalorias() + minimoDesde[0] > maxCalorias) {
            return resultado;
        }

        Busqueda busqueda = new Busqueda(proteina, porCategoria, orden, minimoDesde, maxCalorias, resultado);
        busqueda.explorar(0, proteina.getCalorias());

        Collections.sort(resultado);
        return resultado;
    }

    // guarda el estado de la busqueda para que la recursion sea corta y facil de leer
    private static final class Busqueda {
        private final OpcionMenu proteina;
        private final List<List<OpcionMenu>> porCategoria;
        private final Integer[] orden;
        private final int[] minimoDesde;
        private final int maxCalorias;
        private final List<Combinacion> resultado;
        private final OpcionMenu[] elegidas;   // la posicion i es la opcion de la categoria requerida i

        Busqueda(OpcionMenu proteina, List<List<OpcionMenu>> porCategoria, Integer[] orden,
                 int[] minimoDesde, int maxCalorias, List<Combinacion> resultado) {
            this.proteina = proteina;
            this.porCategoria = porCategoria;
            this.orden = orden;
            this.minimoDesde = minimoDesde;
            this.maxCalorias = maxCalorias;
            this.resultado = resultado;
            this.elegidas = new OpcionMenu[orden.length];
        }

        // nivel: cuantas categorias ya se decidieron, acumulado: calorias hasta ahora
        void explorar(int nivel, int acumulado) {
            if (nivel == orden.length) {
                resultado.add(new Combinacion(proteina, Arrays.asList(elegidas)));
                return;
            }
            for (OpcionMenu opcion : porCategoria.get(orden[nivel])) {
                int nuevo = acumulado + opcion.getCalorias();
                // poda: si ni con lo minimo que falta se cabe, esta y todas las siguientes
                // opciones (que tienen mas calorias) se descartan
                if (nuevo + minimoDesde[nivel + 1] > maxCalorias) {
                    break;
                }
                elegidas[orden[nivel]] = opcion;
                explorar(nivel + 1, nuevo);
            }
        }
    }
}
