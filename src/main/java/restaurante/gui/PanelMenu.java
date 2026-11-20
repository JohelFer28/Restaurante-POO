package restaurante.gui;

import restaurante.dominio.Categoria;
import restaurante.dominio.Menu;
import restaurante.dominio.OpcionMenu;
import restaurante.dominio.Restaurante;
import restaurante.excepciones.RestauranteException;
import restaurante.util.Formato;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class PanelMenu extends JPanel {
    private Restaurante restaurante;

    private final JTextField txtBuscarNombre = new JTextField(14);
    private final JComboBox<String> cmbFiltroCategoria = new JComboBox<>();
    private final JComboBox<String> cmbFiltroEstado = new JComboBox<>(new String[]{"Todas", "Solo disponibles", "No disponibles"});
    private final JTextField txtMinPrecio = new JTextField(6);
    private final JTextField txtMaxPrecio = new JTextField(6);
    private final JTextField txtMinCal = new JTextField(5);
    private final JTextField txtMaxCal = new JTextField(5);

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Categoría", "Descripción", "Calorías", "Precio", "Estado", "En Pedidos"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JTextField txtId = new JTextField(6);
    private final JTextField txtNombre = new JTextField(16);
    private final JComboBox<Categoria> cmbCategoriaForm = new JComboBox<>();
    private final JTextField txtDescripcion = new JTextField(16);
    private final JTextField txtCalorias = new JTextField(8);
    private final JTextField txtPrecio = new JTextField(8);

    public PanelMenu(Restaurante restaurante) {
        this.restaurante = restaurante;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.FONDO_APP);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearBarraBusqueda(), BorderLayout.NORTH);

        EstilosUI.estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFilaSeleccionada();
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(EstilosUI.bordeTarjeta("Catálogo de Opciones del Menú"));

        JPanel panelForm = crearPanelFormulario();
        panelForm.setPreferredSize(new Dimension(380, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelForm);
        split.setResizeWeight(0.68);
        add(split, BorderLayout.CENTER);

        refrescar();
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
        limpiarFormulario();
        refrescar();
    }

    private JPanel crearBarraBusqueda() {
        JPanel barra = new JPanel(new GridLayout(2, 1, 0, 2));
        barra.setBackground(EstilosUI.FONDO_TARJETA);
        barra.setBorder(EstilosUI.bordeTarjeta("Búsqueda y Filtros del Menú (Nombre, Categoría, Precio, Calorías y Disponibilidad)"));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila1.setBackground(EstilosUI.FONDO_TARJETA);
        fila1.add(new JLabel("Nombre:"));
        fila1.add(txtBuscarNombre);
        fila1.add(new JLabel("Categoría:"));
        fila1.add(cmbFiltroCategoria);
        fila1.add(new JLabel("Estado:"));
        fila1.add(cmbFiltroEstado);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila2.setBackground(EstilosUI.FONDO_TARJETA);

        JButton btnBuscar = EstilosUI.botonPrimario("Aplicar Filtros");
        btnBuscar.addActionListener(e -> aplicarFiltros());

        JButton btnLimpiar = EstilosUI.botonSecundario("Mostrar Todo");
        btnLimpiar.addActionListener(e -> {
            txtBuscarNombre.setText("");
            cmbFiltroCategoria.setSelectedIndex(0);
            cmbFiltroEstado.setSelectedIndex(0);
            txtMinPrecio.setText("");
            txtMaxPrecio.setText("");
            txtMinCal.setText("");
            txtMaxCal.setText("");
            cargarTabla(restaurante.getMenu().listarTodas());
        });

        fila2.add(new JLabel("Precio ₡ (mín-máx):"));
        fila2.add(txtMinPrecio);
        fila2.add(new JLabel("-"));
        fila2.add(txtMaxPrecio);
        fila2.add(new JLabel("Kcal (mín-máx):"));
        fila2.add(txtMinCal);
        fila2.add(new JLabel("-"));
        fila2.add(txtMaxCal);
        fila2.add(btnBuscar);
        fila2.add(btnLimpiar);

        barra.add(fila1);
        barra.add(fila2);
        return barra;
    }

    private JPanel crearPanelFormulario() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(EstilosUI.FONDO_TARJETA);
        contenedor.setBorder(EstilosUI.bordeTarjeta("Administración de Opción y Categorías"));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(EstilosUI.FONDO_TARJETA);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtId.setEditable(false);
        txtId.setBackground(EstilosUI.FONDO_APP);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; campos.add(new JLabel("ID (automático):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; campos.add(txtId, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; campos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; campos.add(txtNombre, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; campos.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; campos.add(cmbCategoriaForm, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; campos.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; campos.add(txtDescripcion, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; campos.add(new JLabel("Calorías (kcal):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; campos.add(txtCalorias, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; campos.add(new JLabel("Precio (₡):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; campos.add(txtPrecio, gbc);

        JPanel botones = new JPanel(new GridLayout(3, 2, 6, 6));
        botones.setBackground(EstilosUI.FONDO_TARJETA);
        botones.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

        JButton btnNuevo = EstilosUI.botonExito("Agregar Nueva");
        btnNuevo.addActionListener(e -> agregarOpcion());

        JButton btnGuardar = EstilosUI.botonPrimario("Guardar Cambios");
        btnGuardar.addActionListener(e -> modificarOpcion());

        JButton btnToggle = EstilosUI.botonSecundario("Activar / Desactivar");
        btnToggle.addActionListener(e -> alternarDisponibilidadOpcion());

        JButton btnEliminar = EstilosUI.botonPeligro("Eliminar Opción");
        btnEliminar.addActionListener(e -> eliminarOpcion());

        JButton btnLimpiarForm = EstilosUI.botonSecundario("Limpiar Campos");
        btnLimpiarForm.addActionListener(e -> limpiarFormulario());

        JButton btnCategorias = EstilosUI.botonSecundario("Categorías...");
        btnCategorias.addActionListener(e -> gestionarCategorias());

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnToggle);
        botones.add(btnEliminar);
        botones.add(btnLimpiarForm);
        botones.add(btnCategorias);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setBackground(EstilosUI.FONDO_TARJETA);
        norte.add(campos, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.CENTER);

        JScrollPane scrollForm = new JScrollPane(norte);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(12);
        contenedor.add(scrollForm, BorderLayout.CENTER);
        return contenedor;
    }

    public void refrescar() {
        Menu menu = restaurante.getMenu();
        Object catSel = cmbFiltroCategoria.getSelectedItem();

        cmbFiltroCategoria.removeAllItems();
        cmbFiltroCategoria.addItem("Todas");
        cmbCategoriaForm.removeAllItems();

        for (Categoria c : menu.getCategorias()) {
            cmbFiltroCategoria.addItem(c.getNombre());
            cmbCategoriaForm.addItem(c);
        }
        if (catSel != null) cmbFiltroCategoria.setSelectedItem(catSel);

        cargarTabla(menu.listarTodas());
    }

    private void aplicarFiltros() {
        Menu menu = restaurante.getMenu();
        try {
            List<OpcionMenu> base = new ArrayList<>(menu.listarTodas());

            String texto = txtBuscarNombre.getText().trim();
            if (!texto.isEmpty()) {
                base.retainAll(menu.buscarPorNombre(texto));
            }

            String catNombre = (String) cmbFiltroCategoria.getSelectedItem();
            if (catNombre != null && !"Todas".equals(catNombre)) {
                Categoria cat = menu.buscarCategoria(catNombre);
                base.retainAll(menu.buscarPorCategoria(cat));
            }

            if (!txtMinPrecio.getText().isBlank() || !txtMaxPrecio.getText().isBlank()) {
                int minP = txtMinPrecio.getText().isBlank() ? 0 : Integer.parseInt(txtMinPrecio.getText().trim());
                int maxP = txtMaxPrecio.getText().isBlank() ? Integer.MAX_VALUE : Integer.parseInt(txtMaxPrecio.getText().trim());
                base.retainAll(menu.buscarPorRangoPrecio(minP, maxP));
            }

            if (!txtMinCal.getText().isBlank() || !txtMaxCal.getText().isBlank()) {
                int minC = txtMinCal.getText().isBlank() ? 0 : Integer.parseInt(txtMinCal.getText().trim());
                int maxC = txtMaxCal.getText().isBlank() ? Integer.MAX_VALUE : Integer.parseInt(txtMaxCal.getText().trim());
                base.retainAll(menu.buscarPorRangoCalorias(minC, maxC));
            }

            String estado = (String) cmbFiltroEstado.getSelectedItem();
            if ("Solo disponibles".equals(estado)) {
                base.removeIf(o -> !o.estaDisponible());
            } else if ("No disponibles".equals(estado)) {
                base.removeIf(OpcionMenu::estaDisponible);
            }

            cargarTabla(base);
        } catch (NumberFormatException nfe) {
            mostrarError("Los rangos de precio y calorías deben ser números enteros válidos.");
        } catch (RestauranteException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarTabla(List<OpcionMenu> opciones) {
        modeloTabla.setRowCount(0);
        for (OpcionMenu o : opciones) {
            modeloTabla.addRow(new Object[]{
                    o.getId(),
                    o.getNombre(),
                    o.getCategoria().getNombre() + (o.getCategoria().estaActiva() ? "" : " (Inactiva)"),
                    o.getDescripcion(),
                    o.getCalorias() + " kcal",
                    Formato.colones(o.getPrecio()),
                    o.estaDisponible() ? "Disponible" : "Desactivada",
                    o.estaUsadaEnPedidos() ? "Sí" : "No"
            });
        }
    }

    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        try {
            OpcionMenu o = restaurante.getMenu().obtener(id);
            txtId.setText(String.valueOf(o.getId()));
            txtNombre.setText(o.getNombre());
            cmbCategoriaForm.setSelectedItem(o.getCategoria());
            txtDescripcion.setText(o.getDescripcion());
            txtCalorias.setText(String.valueOf(o.getCalorias()));
            txtPrecio.setText(String.valueOf(o.getPrecio()));
        } catch (RestauranteException ignored) { }
    }

    private void agregarOpcion() {
        try {
            Categoria cat = (Categoria) cmbCategoriaForm.getSelectedItem();
            int cal = Integer.parseInt(txtCalorias.getText().trim());
            int precio = Integer.parseInt(txtPrecio.getText().trim());
            OpcionMenu creada = restaurante.getMenu().agregarOpcion(
                    txtNombre.getText(), cat, txtDescripcion.getText(), cal, precio);
            restaurante.notificarCambioMenu("Opción agregada: " + creada.getNombre());
            limpiarFormulario();
        } catch (NumberFormatException nfe) {
            mostrarError("Calorías y precio deben ser números enteros.");
        } catch (RestauranteException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void modificarOpcion() {
        if (txtId.getText().isBlank()) {
            mostrarError("Seleccione primero una opción de la tabla para modificar.");
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            int cal = Integer.parseInt(txtCalorias.getText().trim());
            int precio = Integer.parseInt(txtPrecio.getText().trim());
            restaurante.getMenu().modificarOpcion(id, txtNombre.getText(), txtDescripcion.getText(), cal, precio);
            restaurante.notificarCambioMenu("Opción #" + id + " modificada.");
        } catch (NumberFormatException nfe) {
            mostrarError("Calorías y precio deben ser números enteros.");
        } catch (RestauranteException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void alternarDisponibilidadOpcion() {
        if (txtId.getText().isBlank()) {
            mostrarError("Seleccione una opción de la tabla.");
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            OpcionMenu o = restaurante.getMenu().obtener(id);
            if (o.estaDisponible()) {
                restaurante.getMenu().desactivarOpcion(id);
            } else {
                restaurante.getMenu().activarOpcion(id);
            }
            restaurante.notificarCambioMenu("Disponibilidad actualizada para: " + o.getNombre());
        } catch (RestauranteException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarOpcion() {
        if (txtId.getText().isBlank()) {
            mostrarError("Seleccione una opción de la tabla para eliminar.");
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            restaurante.getMenu().eliminarOpcion(id);
            limpiarFormulario();
            restaurante.notificarCambioMenu("Opción #" + id + " eliminada del menú.");
        } catch (RestauranteException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void gestionarCategorias() {
        Menu menu = restaurante.getMenu();
        String[] opciones = {"Agregar nueva categoría", "Activar / Desactivar categoría existente", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this,
                "Seleccione la operación sobre categorías del menú:",
                "Administración de Categorías",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

        try {
            if (sel == 0) {
                String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva categoría:");
                if (nombre != null) {
                    Categoria c = menu.agregarCategoria(nombre);
                    restaurante.notificarCambioMenu("Categoría agregada: " + c.getNombre());
                }
            } else if (sel == 1) {
                Categoria[] cats = menu.getCategorias().toArray(new Categoria[0]);
                Categoria elegida = (Categoria) JOptionPane.showInputDialog(this,
                        "Elija la categoría para cambiar su estado activo/inactivo:",
                        "Estado de Categoría", JOptionPane.QUESTION_MESSAGE, null, cats, cats[0]);
                if (elegida != null) {
                    if (elegida.estaActiva()) {
                        menu.desactivarCategoria(elegida);
                    } else {
                        menu.activarCategoria(elegida);
                    }
                    restaurante.notificarCambioMenu("Categoría " + elegida.getNombre()
                            + (elegida.estaActiva() ? " activada." : " desactivada."));
                }
            }
        } catch (RestauranteException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCalorias.setText("");
        txtPrecio.setText("");
        tabla.clearSelection();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación de Menú", JOptionPane.WARNING_MESSAGE);
    }
}