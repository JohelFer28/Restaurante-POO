package restaurante.util;

import java.util.Locale;

public final class Formato {
    private Formato() { }

    // el dinero se guarda como int en colones
    // ejemplo: 2500 se muestra como ₡2 500
    public static String colones(int monto) {
        return "₡" + String.format(Locale.US, "%,d", monto).replace(',', ' ');
    }
}
