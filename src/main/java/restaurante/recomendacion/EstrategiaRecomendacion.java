package restaurante.recomendacion;

import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;

import java.util.List;

// interfaz del patron Strategy: define QUE se calcula (las comidas completas bajo un limite de calorias)
// y cada clase que la implemente define COMO. ServicioRecomendacion puede usar cualquiera sin cambiar
public interface EstrategiaRecomendacion {

    // devuelve las combinaciones de una opcion por cada categoria requerida, mas la proteina,
    // cuya suma de calorias es menor o igual a maxCalorias
    // el resultado sale ordenado: calorias ascendentes y luego por nombre
    List<Combinacion> generar(Menu menu, OpcionMenu proteina, List<Categoria> requeridas, int maxCalorias);
}
