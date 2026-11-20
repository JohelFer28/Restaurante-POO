package restaurante.gui;

import restaurante.datos.DatosDemo;
import restaurante.dominio.Restaurante;
import restaurante.observador.BitacoraEventos;
import restaurante.observador.EventoRestaurante;
import restaurante.observador.ObservadorRestaurante;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class VentanaPrincipal extends JFrame implements ObservadorRestaurante {
    private Restaurante restaurante;
    private final BitacoraEventos bitacora;

    private final PanelMesas panelMesas;
    private final PanelMenu panelMenu;
    private final PanelRecomendacion panelRecomendacion;
    private final PanelEstadisticas panelEstadisticas;
    private final PanelHistorial panelHistorial;
    private final JLabel lblBarraEstado = new JLabel("Sistema iniciado con datos de demostración.");

    public VentanaPrincipal(Restaurante restaurante, BitacoraEventos bitacora) {
        super("Sistema Inteligente de Atención y Menú — Restaurante OO (TEC)");
        this.restaurante = restaurante;
        this.bitacora = bitacora;
        this.restaurante.agregarObservador(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 760);
        setMinimumSize(new Dimension(1024, 660));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 23, 42));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lblBrand = new JLabel("Restaurante OO  |  Gestión de Mesas, Pedidos, Cobros y Filtro Calórico");
        lblBrand.setFont(EstilosUI.FUENTE_TITULO);
        lblBrand.setForeground(Color.WHITE);

        JButton btnReiniciar = EstilosUI.botonSecundario("Restaurar Datos Demo");
        btnReiniciar.addActionListener(e -> reiniciarDatosDemo());

        topBar.add(lblBrand, BorderLayout.WEST);
        topBar.add(btnReiniciar, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        panelMesas = new PanelMesas(restaurante);
        panelMenu = new PanelMenu(restaurante);
        panelRecomendacion = new PanelRecomendacion(restaurante);
        panelEstadisticas = new PanelEstadisticas(restaurante);
        panelHistorial = new PanelHistorial(restaurante, bitacora);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(EstilosUI.FUENTE_NEGRITA);
        pestanas.addTab("1. Salón y Mesas", panelMesas);
        pestanas.addTab("2. Menú e Inventario", panelMenu);
        pestanas.addTab("3. Recomendador Calórico", panelRecomendacion);
        pestanas.addTab("4. Estadísticas y Reportes", panelEstadisticas);
        pestanas.addTab("5. Historial y Bitácora", panelHistorial);
        add(pestanas, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(EstilosUI.FONDO_TARJETA);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, EstilosUI.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        lblBarraEstado.setFont(EstilosUI.FUENTE_PEQUENA);
        lblBarraEstado.setForeground(EstilosUI.TEXTO_OSCURO);
        statusBar.add(lblBarraEstado, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    @Override
    public void alOcurrirEvento(EventoRestaurante evento) {
        lblBarraEstado.setText("Último evento: " + evento);
        panelMesas.refrescar();
        panelMenu.refrescar();
        panelRecomendacion.refrescar();
        panelEstadisticas.refrescar();
        panelHistorial.refrescar();
    }

    private void reiniciarDatosDemo() {
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea restaurar las mesas, el menú y el historial de demostración iniciales?",
                "Restaurar Datos Demo", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        bitacora.limpiar();
        this.restaurante = DatosDemo.crearRestauranteConHistorialDemo(bitacora);
        this.restaurante.agregarObservador(this);

        panelMesas.setRestaurante(restaurante);
        panelMenu.setRestaurante(restaurante);
        panelRecomendacion.setRestaurante(restaurante);
        panelEstadisticas.setRestaurante(restaurante);
        panelHistorial.setRestaurante(restaurante);
        lblBarraEstado.setText("Datos de demostración restaurados correctamente.");
    }
}
