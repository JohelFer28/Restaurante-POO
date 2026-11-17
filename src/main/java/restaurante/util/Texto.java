package restaurante.util;

import java.text.Normalizer;

public final class Texto {
    private Texto() { }

    // quita tildes y mayusculas para comparar texto
    public static String normalizar(String s) {
        String sinTildes = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim();
    }
}
