package restaurante.gui;

import restaurante.dominio.Restaurante;
import restaurante.dominio.Visita;
import restaurante.observador.BitacoraEventos;
import restaurante.observador.EventoRestaurante;
import restaurante.util.Formato;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelHistorial extends JPanel {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Restaurante restaurante;
    private final BitacoraEventos bitacora;

    private final DefaultTableModel modHistorial = new DefaultTableModel(
            new String[]{"#", "Mesa", "Mesero", "Apertura", "Cierre", "Comensales", "Modalidad", "Total Cobrado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tablaHistorial = new JTable(modHistorial);
    private final JTextArea txtBitacora = new JTextArea();

    public PanelHistorial(Restaurante restaurante, BitacoraEventos bitacora) {
        this.restaurante = restaurante;
        this.bitacora = bitacora;

        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.FONDO_APP);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelTop = new JPanel(new BorderLayout(6, 6));
        panelTop.setBackground(EstilosUI.FONDO_TARJETA);
        panelTop.setBorder(EstilosUI.bordeTarjeta("Historial de Visitas Cerradas"));

        EstilosUI.estilizarTabla(tablaHistorial);
        panelTop.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        JPanel accionesTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accionesTop.setBackground(EstilosUI.FONDO_TARJETA);
        JButton btnVerDetalle = EstilosUI.botonPrimario("Ver Recibo / Resumen Completo");
        btnVerDetalle.addActionListener(e -> mostrarDetalleVisita());
        accionesTop.add(btnVerDetalle);
        panelTop.add(accionesTop, BorderLayout.SOUTH);

        JPanel panelBottom = new JPanel(new BorderLayout(6, 6));
        panelBottom.setBackground(EstilosUI.FONDO_TARJETA);
        panelBottom.setBorder(EstilosUI.bordeTarjeta("Bitácora de Auditoría en Tiempo Real (Patrón Observer)"));

        txtBitacora.setEditable(false);
        txtBitacora.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelBottom.add(new JScrollPane(txtBitacora), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelTop, panelBottom);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        refrescar();
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
        refrescar();
    }

    public void refrescar() {
        modHistorial.setRowCount(0);
        List<Visita> historial = restaurante.getHistorial();
        for (int i = 0; i < historial.size(); i++) {
            Visita v = historial.get(i);
            modHistorial.addRow(new Object[]{
                    i + 1,
                    "Mesa #" + v.getNumeroMesa(),
                    v.getMesero().getNombre(),
                    v.getInicio().format(FMT),
                    v.getCierre() != null ? v.getCierre().format(FMT) : "-",
                    v.getComensales().size(),
                    v.getModalidad() != null ? v.getModalidad().getDescripcion() : "-",
                    Formato.colones(v.totalMesa())
            });
        }

        StringBuilder sb = new StringBuilder();
        List<EventoRestaurante> eventos = bitacora.getEventos();
        for (int i = eventos.size() - 1; i >= 0; i--) {
            sb.append(eventos.get(i)).append("\n");
        }
        txtBitacora.setText(sb.toString());
        txtBitacora.setCaretPosition(0);
    }

    private void mostrarDetalleVisita() {
        int fila = tablaHistorial.getSelectedRow();
        List<Visita> historial = restaurante.getHistorial();
        if (fila < 0 || fila >= historial.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una visita cerrada de la tabla.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JTextArea area = new JTextArea(historial.get(fila).resumen(), 18, 52);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Resumen Detallado de Visita", JOptionPane.INFORMATION_MESSAGE);
    }
}
