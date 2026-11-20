package restaurante.gui;

import restaurante.dominio.Categoria;
import restaurante.dominio.Comensal;
import restaurante.dominio.Menu;
import restaurante.dominio.Mesa;
import restaurante.dominio.OpcionMenu;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.RestauranteException;
import restaurante.recomendacion.Combinacion;
import restaurante.recomendacion.EstrategiaRecomendacion;
import restaurante.recomendacion.RecomendacionFuerzaBruta;
import restaurante.recomendacion.RecomendacionPorPoda;
import restaurante.recomendacion.ResultadoRecomendacion;
import restaurante.recomendacion.ServicioRecomendacion;
import restaurante.util.Formato;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class PanelRecomendacion extends JPanel {
    private Restaurante restaurante;
    private List<Combinacion> ultimasCombinaciones = List.of();

    private final JComboBox<OpcionMenu> cmbProteina = new JComboBox<>();
    private final JSpinner spMaxCalorias = new JSpinner(new SpinnerNumberModel(800, 0, 10000, 25));
    private final JComboBox<String> cmbEstrategia = new JComboBox<>(new String[]{
            "Backtracking con Poda (Algoritmo Principal)",
            "Fuerza Bruta (Referencia Comparativa)"
    });
    private final JPanel panelCheckCategorias = new JPanel();
    private final List<JCheckBox> checksCategorias = new ArrayList<>();

    private final JLabel lblResumen = new JLabel("Configure una proteína y límite calórico para generar combinaciones.");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"#", "Proteína Base", "Acompañamientos / Categorías", "Total Calorías", "Precio Total"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public PanelRecomendacion(Restaurante restaurante) {
        this.restaurante = restaurante;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.FONDO_APP);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelIzq = crearPanelConfiguracion();
        panelIzq.setPreferredSize(new Dimension(350, 0));

        JPanel panelDer = crearPanelResultados();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setResizeWeight(0.32);
        add(split, BorderLayout.CENTER);

        refrescar();
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
        refrescar();
    }

    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstilosUI.FONDO_TARJETA);
        panel.setBorder(EstilosUI.bordeTarjeta("Parámetros de Comida Completa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y++;
        panel.add(new JLabel("1. Proteína seleccionada:"), gbc);

        gbc.gridy = y++;
        panel.add(cmbProteina, gbc);

        gbc.gridy = y++;
        panel.add(new JLabel("2. Máximo de calorías permitidas (X kcal):"), gbc);

        gbc.gridy = y++;
        panel.add(spMaxCalorias, gbc);

        gbc.gridy = y++;
        panel.add(new JLabel("3. Categorías adicionales requeridas:"), gbc);

        panelCheckCategorias.setLayout(new BoxLayout(panelCheckCategorias, BoxLayout.Y_AXIS));
        panelCheckCategorias.setBackground(EstilosUI.FONDO_TARJETA);
        JScrollPane scrollChecks = new JScrollPane(panelCheckCategorias);
        scrollChecks.setPreferredSize(new Dimension(0, 135));
        gbc.gridy = y++;
        panel.add(scrollChecks, gbc);

        gbc.gridy = y++;
        panel.add(new JLabel("4. Estrategia de búsqueda (Patrón Strategy):"), gbc);

        gbc.gridy = y++;
        panel.add(cmbEstrategia, gbc);

        JButton btnBuscar = EstilosUI.botonPrimario("Calcular Combinaciones");
        btnBuscar.addActionListener(e -> ejecutarRecomendacion());
        gbc.gridy = y++;
        panel.add(btnBuscar, gbc);

        JLabel nota = new JLabel("<html><body style='width:270px;color:#64748b'>Regla: 1 opción por cada categoría requerida + la proteína, sin repetidos y ordenadas por calorías ascendentes.</body></html>");
        nota.setFont(EstilosUI.FUENTE_PEQUENA);
        gbc.gridy = y; gbc.weighty = 1.0;
        panel.add(nota, gbc);

        return panel;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(EstilosUI.FONDO_TARJETA);
        panel.setBorder(EstilosUI.bordeTarjeta("Combinaciones Válidas Encontradas"));

        lblResumen.setFont(EstilosUI.FUENTE_NEGRITA);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        panel.add(lblResumen, BorderLayout.NORTH);

        EstilosUI.estilizarTabla(tabla);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setBackground(EstilosUI.FONDO_TARJETA);
        JButton btnPedirCombinacion = EstilosUI.botonExito("Agregar Combinación Seleccionada a Mesa Abierta");
        btnPedirCombinacion.addActionListener(e -> cargarCombinacionEnMesa());
        pie.add(btnPedirCombinacion);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    public void refrescar() {
        Menu menu = restaurante.getMenu();
        cmbProteina.removeAllItems();
        panelCheckCategorias.removeAll();
        checksCategorias.clear();

        try {
            Categoria catProt = menu.buscarCategoria(Menu.NOMBRE_PROTEINAS);
            for (OpcionMenu o : menu.opcionesDisponibles(catProt)) {
                cmbProteina.addItem(o);
            }
            for (Categoria c : menu.getCategoriasActivas()) {
                if (!c.equals(catProt)) {
                    JCheckBox chk = new JCheckBox(c.getNombre() + " (" + menu.opcionesDisponibles(c).size() + " disp.)", true);
                    chk.setBackground(EstilosUI.FONDO_TARJETA);
                    chk.putClientProperty("categoria", c);
                    checksCategorias.add(chk);
                    panelCheckCategorias.add(chk);
                }
            }
        } catch (RestauranteException ignored) { }

        panelCheckCategorias.revalidate();
        panelCheckCategorias.repaint();
    }

    private void ejecutarRecomendacion() {
        Menu menu = restaurante.getMenu();
        OpcionMenu proteina = (OpcionMenu) cmbProteina.getSelectedItem();
        int maxCal = (Integer) spMaxCalorias.getValue();

        List<Categoria> requeridas = new ArrayList<>();
        for (JCheckBox chk : checksCategorias) {
            if (chk.isSelected()) {
                requeridas.add((Categoria) chk.getClientProperty("categoria"));
            }
        }

        EstrategiaRecomendacion estrategia = cmbEstrategia.getSelectedIndex() == 1
                ? new RecomendacionFuerzaBruta()
                : new RecomendacionPorPoda();

        ServicioRecomendacion servicio = new ServicioRecomendacion(estrategia);
        try {
            long t0 = System.nanoTime();
            ResultadoRecomendacion res = servicio.recomendar(menu, proteina, maxCal, requeridas);
            long micros = (System.nanoTime() - t0) / 1_000;

            ultimasCombinaciones = res.getCombinaciones();
            modeloTabla.setRowCount(0);

            int idx = 1;
            for (Combinacion c : ultimasCombinaciones) {
                StringBuilder detalle = new StringBuilder();
                for (OpcionMenu op : c.getOpciones()) {
                    if (detalle.length() > 0) detalle.append(" + ");
                    detalle.append(op.getNombre()).append(" (").append(op.getCalorias()).append(" kcal)");
                }
                modeloTabla.addRow(new Object[]{
                        idx++,
                        c.getProteina().getNombre() + " (" + c.getProteina().getCalorias() + " kcal)",
                        detalle.length() == 0 ? "Solo proteína" : detalle.toString(),
                        c.getTotalCalorias() + " kcal",
                        Formato.colones(c.getTotalPrecio())
                });
            }

            lblResumen.setText(res.getMensaje() + "  [Tiempo: " + micros + " µs]");
            lblResumen.setForeground(res.hayResultados() ? EstilosUI.EXITO.darker() : EstilosUI.PELIGRO);
        } catch (RestauranteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación de Filtro Calórico", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarCombinacionEnMesa() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= ultimasCombinaciones.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una combinación de la tabla primero.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<Mesa> ocupadas = new ArrayList<>();
        for (Mesa m : restaurante.getMesas()) {
            if ("Ocupada".equals(m.getNombreEstado())) ocupadas.add(m);
        }
        if (ocupadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay mesas en estado 'Ocupada' para cargar el pedido.",
                    "Sin mesas activas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mesa mesa = (Mesa) JOptionPane.showInputDialog(this,
                "Seleccione la mesa abierta que recibirá esta combinación:",
                "Cargar Combinación a Mesa", JOptionPane.QUESTION_MESSAGE, null,
                ocupadas.toArray(new Mesa[0]), ocupadas.get(0));
        if (mesa == null) return;

        List<Comensal> comensales = mesa.getVisitaActual().getComensales();
        Comensal comensal = (Comensal) JOptionPane.showInputDialog(this,
                "¿Qué comensal solicita esta combinación?",
                "Seleccionar Comensal", JOptionPane.QUESTION_MESSAGE, null,
                comensales.toArray(new Comensal[0]), comensales.get(0));
        if (comensal == null) return;

        Combinacion elegida = ultimasCombinaciones.get(fila);
        try {
            for (OpcionMenu op : elegida.getTodas()) {
                restaurante.agregarPedido(mesa.getNumero(), comensal.getAlias(), op.getId(), 1);
            }
            JOptionPane.showMessageDialog(this,
                    "Se agregaron los " + elegida.getTodas().size() + " artículos de la combinación al pedido de "
                            + comensal.getAlias() + " en Mesa #" + mesa.getNumero() + ".",
                    "Pedido Agregado", JOptionPane.INFORMATION_MESSAGE);
        } catch (RestauranteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al pedir", JOptionPane.WARNING_MESSAGE);
        }
    }
}
