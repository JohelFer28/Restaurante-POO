package restaurante.util;

import java.util.Locale;

/** Da formato a los montos. El dinero se guarda como int en colones (sin decimales). */
public final class Formato {
    private Formato() { }

    /** 2500 -> "₡2 500" */
    public static String colones(int monto) {
        return "₡" + String.format(Locale.US, "%,d", monto).replace(',', ' ');
    }
}
