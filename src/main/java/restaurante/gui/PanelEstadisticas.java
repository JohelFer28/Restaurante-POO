package restaurante.gui;

import restaurante.dominio.Categoria;
import restaurante.dominio.Restaurante;
import restaurante.estadisticas.ServicioEstadisticas;
import restaurante.estadisticas.ServicioEstadisticas.IngresoCategoria;
import restaurante.estadisticas.ServicioEstadisticas.ItemMasSolicitado;
import restaurante.estadisticas.ServicioEstadisticas.RendimientoMesero;
import restaurante.estadisticas.ServicioEstadisticas.ReporteVentas;
import restaurante.estadisticas.ServicioEstadisticas.ReporteVisitas;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Formato;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class PanelEstadisticas extends JPanel {
    private Restaurante restaurante;

    private final JTextField txtDesde = new JTextField(LocalDate.now().minusDays(7).toString(), 10);
    private final JTextField txtHasta = new JTextField(LocalDate.now().toString(), 10);
    private final JComboBox<String> cmbCategoria = new JComboBox<>();

    private final JLabel lblKpiVentas = new JLabel("₡0");
    private final JLabel lblKpiVisitas = new JLabel("0");
    private final JLabel lblKpiTicket = new JLabel("₡0");
    private final JLabel lblKpiTop = new JLabel("-");
    private final JLabel lblExplicacion = new JLabel(" ");

    private final DefaultTableModel modTopItems = new DefaultTableModel(
            new String[]{"Pos.", "Artículo", "Categoría", "Unidades", "Monto Generado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel modMetodosPago = new DefaultTableModel(
            new String[]{"Método de Pago", "Cant. Pagos", "Total Cobrado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel modMeseros = new DefaultTableModel(
            new String[]{"Mesero", "Visitas Cerradas", "Comensales", "Total Facturado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel modCategorias = new DefaultTableModel(
            new String[]{"Categoría", "Unidades", "Ingresos", "Participación"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public PanelEstadisticas(Restaurante restaurante) {
        this.restaurante = restaurante;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.FONDO_APP);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearBarraFiltros(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(EstilosUI.FONDO_APP);

        JPanel kpis = new JPanel(new GridLayout(1, 4, 10, 10));
        kpis.setBackground(EstilosUI.FONDO_APP);
        kpis.add(EstilosUI.crearTarjetaKpi("Ventas Cobradas (Rango)", lblKpiVentas, EstilosUI.EXITO));
        kpis.add(EstilosUI.crearTarjetaKpi("Visitas Cerradas", lblKpiVisitas, EstilosUI.PRIMARIO));
        kpis.add(EstilosUI.crearTarjetaKpi("Ticket Promedio / Visita", lblKpiTicket, EstilosUI.ADVERTENCIA));
        kpis.add(EstilosUI.crearTarjetaKpi("Artículo Más Pedido", lblKpiTop, new Color(124, 58, 237)));
        centro.add(kpis, BorderLayout.NORTH);

        JPanel gridReportes = new JPanel(new GridLayout(2, 2, 10, 10));
        gridReportes.setBackground(EstilosUI.FONDO_APP);
        gridReportes.add(envolverTabla(modTopItems, "1. Artículos Más Solicitados (Global / por Categoría)"));
        gridReportes.add(envolverTabla(modMetodosPago, "2. Ventas Cobradas por Método de Pago"));
        gridReportes.add(envolverTabla(modMeseros, "3. Rendimiento de Visitas Cerradas por Mesero"));
        gridReportes.add(envolverTabla(modCategorias, "4. Ingresos por Categoría de Menú"));
        centro.add(gridReportes, BorderLayout.CENTER);

        lblExplicacion.setFont(EstilosUI.FUENTE_PEQUENA);
        lblExplicacion.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        centro.add(lblExplicacion, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);
        refrescar();
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
        refrescar();
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barra.setBackground(EstilosUI.FONDO_TARJETA);
        barra.setBorder(EstilosUI.bordeTarjeta("Filtro de Rango de Fechas (yyyy-MM-dd) y Categoría"));

        JButton btnConsultar = EstilosUI.botonPrimario("Actualizar Estadísticas");
        btnConsultar.addActionListener(e -> calcular());

        JButton btnHoy = EstilosUI.botonSecundario("Hoy");
        btnHoy.addActionListener(e -> {
            String hoy = LocalDate.now().toString();
            txtDesde.setText(hoy);
            txtHasta.setText(hoy);
            calcular();
        });

        JButton btnSemana = EstilosUI.botonSecundario("Últimos 7 días");
        btnSemana.addActionListener(e -> {
            txtDesde.setText(LocalDate.now().minusDays(7).toString());
            txtHasta.setText(LocalDate.now().toString());
            calcular();
        });

        barra.add(new JLabel("Desde:"));
        barra.add(txtDesde);
        barra.add(new JLabel("Hasta:"));
        barra.add(txtHasta);
        barra.add(new JLabel("Categoría (Top artículos):"));
        barra.add(cmbCategoria);
        barra.add(btnConsultar);
        barra.add(btnHoy);
        barra.add(btnSemana);
        return barra;
    }

    private JScrollPane envolverTabla(DefaultTableModel modelo, String titulo) {
        JTable t = new JTable(modelo);
        EstilosUI.estilizarTabla(t);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(EstilosUI.bordeTarjeta(titulo));
        return sp;
    }

    public void refrescar() {
        Object sel = cmbCategoria.getSelectedItem();
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem("Todas las categorías");
        for (Categoria c : restaurante.getMenu().getCategorias()) {
            cmbCategoria.addItem(c.getNombre());
        }
        if (sel != null) cmbCategoria.setSelectedItem(sel);
        calcular();
    }

    private void calcular() {
        try {
            LocalDate desde = txtDesde.getText().isBlank() ? null : LocalDate.parse(txtDesde.getText().trim());
            LocalDate hasta = txtHasta.getText().isBlank() ? null : LocalDate.parse(txtHasta.getText().trim());

            Categoria catFiltro = null;
            String catNombre = (String) cmbCategoria.getSelectedItem();
            if (catNombre != null && !"Todas las categorías".equals(catNombre)) {
                catFiltro = restaurante.getMenu().buscarCategoria(catNombre);
            }

            ServicioEstadisticas stats = new ServicioEstadisticas(restaurante);
            ReporteVentas ventas = stats.ventasCobradas(desde, hasta);
            List<ItemMasSolicitado> top = stats.articulosMasSolicitados(desde, hasta, catFiltro);
            ReporteVisitas visitas = stats.resumenVisitasCerradas(desde, hasta);
            List<IngresoCategoria> categorias = stats.ingresosPorCategoria(desde, hasta);

            lblKpiVentas.setText(Formato.colones(ventas.getTotalCobrado()));
            lblKpiVisitas.setText(String.valueOf(visitas.getVisitasCerradas()));
            lblKpiTicket.setText(Formato.colones(visitas.getTicketPromedio()));
            lblKpiTop.setText(top.isEmpty() ? "Sin datos"
                    : top.get(0).getNombreOpcion() + " (" + top.get(0).getUnidadesVendidas() + ")");

            modTopItems.setRowCount(0);
            int pos = 1;
            for (ItemMasSolicitado item : top) {
                modTopItems.addRow(new Object[]{
                        pos++, item.getNombreOpcion(), item.getNombreCategoria(),
                        item.getUnidadesVendidas(), Formato.colones(item.getMontoTotal())
                });
            }

            modMetodosPago.setRowCount(0);
            for (Map.Entry<String, Integer> entry : ventas.getCobradoPorMetodo().entrySet()) {
                String metodo = entry.getKey();
                modMetodosPago.addRow(new Object[]{
                        metodo,
                        ventas.getPagosPorMetodo().getOrDefault(metodo, 0),
                        Formato.colones(entry.getValue())
                });
            }

            modMeseros.setRowCount(0);
            for (RendimientoMesero rm : visitas.getPorMesero()) {
                modMeseros.addRow(new Object[]{
                        rm.getMesero(), rm.getVisitasAtendidas(),
                        rm.getComensalesAtendidos(), Formato.colones(rm.getTotalFacturado())
                });
            }

            modCategorias.setRowCount(0);
            for (IngresoCategoria ic : categorias) {
                modCategorias.addRow(new Object[]{
                        ic.getCategoria(), ic.getUnidades(),
                        Formato.colones(ic.getMonto()),
                        String.format("%.1f%%", ic.getPorcentaje())
                });
            }

            lblExplicacion.setText(ventas.getExplicacion() + " | " + visitas.getExplicacion());
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use AAAA-MM-DD (ejemplo: 2026-11-20).",
                    "Fecha Inválida", JOptionPane.WARNING_MESSAGE);
        } catch (DatoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Rango Inválido", JOptionPane.WARNING_MESSAGE);
        }
    }
}
