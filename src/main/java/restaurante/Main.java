package restaurante;

import restaurante.datos.DatosDemo;
import restaurante.dominio.Restaurante;
import restaurante.gui.VentanaPrincipal;
import restaurante.observador.BitacoraEventos;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// punto de entrada de la aplicacion: arranca la interfaz grafica en el hilo de eventos de Swing
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // se intenta aplicar Nimbus para que los controles se vean mas modernos que Metal
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // si el sistema operativo no trae Nimbus usa el estilo por defecto sin fallar
            }

            BitacoraEventos bitacora = new BitacoraEventos();
            Restaurante restaurante = DatosDemo.crearRestauranteConHistorialDemo(bitacora);
            VentanaPrincipal ventana = new VentanaPrincipal(restaurante, bitacora);
            ventana.setVisible(true);
        });
    }
}
