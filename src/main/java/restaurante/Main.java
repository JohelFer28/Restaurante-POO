package restaurante;

import restaurante.datos.DatosDemo;
import restaurante.dominio.Restaurante;
import restaurante.gui.VentanaPrincipal;
import restaurante.observador.BitacoraEventos;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // usa el LookAndFeel por defecto si Nimbus no esta disponible
            }

            BitacoraEventos bitacora = new BitacoraEventos();
            Restaurante restaurante = DatosDemo.crearRestauranteConHistorialDemo(bitacora);
            VentanaPrincipal ventana = new VentanaPrincipal(restaurante, bitacora);
            ventana.setVisible(true);
        });
    }
}
