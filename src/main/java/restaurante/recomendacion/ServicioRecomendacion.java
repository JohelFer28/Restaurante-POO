package restaurante.recomendacion;

import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;
import restaurante.excepciones.DatoInvalidoException;

import java.util.ArrayList;
import java.util.List;

// valida lo que pide el cliente, le pasa el trabajo a la estrategia y explica el resultado
// regla del equipo: una opcion por categoria requerida, mas la proteina elegida
// categorias requeridas: todas las activas menos proteinas (o las que el cliente escoja)
public class ServicioRecomendacion {
    private final EstrategiaRecomendacion estrategia;

    // la estrategia se recibe de afuera, asi se puede cambiar sin tocar esta clase
    public ServicioRecomendacion(EstrategiaRecomendacion estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser nula.");
        }
        this.estrategia = estrategia;
    }

    // usa todas las categorias activas que no son proteinas
    public ResultadoRecomendacion recomendar(Menu menu, OpcionMenu proteina, int maxCalorias)
            throws DatoInvalidoException {
        Categoria proteinas = menu.buscarCategoria(Menu.NOMBRE_PROTEINAS);
        List<Categoria> requeridas = new ArrayList<>();
        for (Categoria c : menu.getCategoriasActivas()) {
            if (!c.equals(proteinas)) requeridas.add(c);
        }
        return recomendar(menu, proteina, maxCalorias, requeridas);
    }

    // el cliente escoge cuales categorias quiere en su comida
    public ResultadoRecomendacion recomendar(Menu menu, OpcionMenu proteina, int maxCalorias,
                                             List<Categoria> requeridas) throws DatoInvalidoException {
        validar(menu, proteina, maxCalorias, requeridas);

        List<Combinacion> combinaciones = estrategia.generar(menu, proteina, requeridas, maxCalorias);
        if (!combinaciones.isEmpty()) {
            return new ResultadoRecomendacion(combinaciones,
                    "Se encontraron " + combinaciones.size() + " combinaciones de hasta " + maxCalorias + " kcal.");
        }
        return new ResultadoRecomendacion(combinaciones, explicarSinResultados(menu, proteina, maxCalorias, requeridas));
    }

    private void validar(Menu menu, OpcionMenu proteina, int maxCalorias, List<Categoria> requeridas)
            throws DatoInvalidoException {
        if (maxCalorias < 0) {
            throw new DatoInvalidoException("El máximo de calorías no puede ser negativo.");
        }
        if (proteina == null) {
            throw new DatoInvalidoException("Debe elegir una proteína.");
        }
        Categoria proteinas = menu.buscarCategoria(Menu.NOMBRE_PROTEINAS);
        if (!proteina.getCategoria().equals(proteinas)) {
            throw new DatoInvalidoException("\"" + proteina.getNombre() + "\" no es una proteína.");
        }
        if (!proteina.estaDisponible()) {
            throw new DatoInvalidoException("La proteína \"" + proteina.getNombre() + "\" no está disponible.");
        }
        if (requeridas == null) {
            throw new DatoInvalidoException("Debe indicar las categorías de la comida.");
        }
        List<Categoria> vistas = new ArrayList<>();
        for (Categoria c : requeridas) {
            if (c == null || !menu.getCategorias().contains(c)) {
                throw new DatoInvalidoException("Hay una categoría que no existe en el menú.");
            }
            if (c.equals(proteinas)) {
                throw new DatoInvalidoException("Las proteínas no se eligen como categoría adicional.");
            }
            if (!c.estaActiva()) {
                throw new DatoInvalidoException("La categoría \"" + c + "\" está desactivada.");
            }
            if (vistas.contains(c)) {
                throw new DatoInvalidoException("La categoría \"" + c + "\" está repetida.");
            }
            vistas.add(c);
        }
    }

    // le dice al cliente por que no hay resultados y que puede cambiar
    private String explicarSinResultados(Menu menu, OpcionMenu proteina, int maxCalorias, List<Categoria> requeridas) {
        int minimo = proteina.getCalorias();
        for (Categoria c : requeridas) {
            List<OpcionMenu> disponibles = menu.opcionesDisponibles(c);
            if (disponibles.isEmpty()) {
                return "No hay combinaciones: la categoría \"" + c + "\" no tiene opciones disponibles. "
                        + "Active alguna opción o quite la categoría de la selección.";
            }
            int menor = disponibles.get(0).getCalorias();
            for (OpcionMenu o : disponibles) {
                if (o.getCalorias() < menor) menor = o.getCalorias();
            }
            minimo += menor;
        }
        return "No hay combinaciones de hasta " + maxCalorias + " kcal. La comida más liviana con \""
                + proteina.getNombre() + "\" tiene " + minimo + " kcal. Suba el límite o cambie las categorías.";
    }
}
