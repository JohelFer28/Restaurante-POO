package restaurante.util;

import java.text.Normalizer;

/** Utilidades para comparar texto sin importar mayusculas ni tildes. */
public final class Texto {
    private Texto() { }

    /** "Proteínas " -> "proteinas". Sirve para que buscar "proteina" encuentre "Proteínas". */
    public static String normalizar(String s) {
        String sinTildes = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim();
    }
}
