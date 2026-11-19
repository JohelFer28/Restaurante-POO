package restaurante.dominio;

// persona sentada en la mesa, se identifica con un alias unico dentro de la visita
// en la parte 3 aqui se van a guardar sus pedidos
public class Comensal {
    private final String alias;

    // sin public: solo Visita crea comensales porque ella valida que el alias no se repita
    Comensal(String alias) {
        this.alias = alias;
    }

    public String getAlias() { return alias; }

    @Override
    public String toString() { return alias; }
}
