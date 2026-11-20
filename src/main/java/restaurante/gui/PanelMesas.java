package restaurante.gui;

import restaurante.dominio.Categoria;
import restaurante.dominio.Comensal;
import restaurante.dominio.LineaPedido;
import restaurante.dominio.Mesa;
import restaurante.dominio.Mesero;
import restaurante.dominio.MetodoPago;
import restaurante.dominio.ModalidadCuenta;
import restaurante.dominio.OpcionMenu;
import restaurante.dominio.Pago;
import restaurante.dominio.PagoEfectivo;
import restaurante.dominio.PagoSinpe;
import restaurante.dominio.PagoTarjeta;
import restaurante.dominio.Restaurante;
import restaurante.dominio.Visita;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.excepciones.RestauranteException;
import restaurante.util.Formato;
import javax.swing.JTextArea;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelMesas extends JPanel {
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Restaurante restaurante;
    private int mesaSeleccionada = 1;

    private final JPanel gridMesas = new JPanel(new GridLayout(0, 2, 10, 10));
    private final JPanel contenedorDetalle = new JPanel(new BorderLayout());

    public PanelMesas(Restaurante restaurante) {
        this.restaurante = restaurante;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.FONDO_APP);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelIzquierdo = new JPanel(new BorderLayout(8, 8));
        panelIzquierdo.setBackground(EstilosUI.FONDO_APP);
        panelIzquierdo.setBorder(EstilosUI.bordeTarjeta("Mapa de Mesas (Seleccione una mesa)"));
        panelIzquierdo.setPreferredSize(new Dimension(370, 0));

        gridMesas.setBackground(EstilosUI.FONDO_APP);
        JScrollPane scrollMesas = new JScrollPane(gridMesas);
        scrollMesas.setBorder(null);
        panelIzquierdo.add(scrollMesas, BorderLayout.CENTER);

        JLabel leyenda = new JLabel("Verde: Disponible  |  Ámbar: Ocupada  |  Rojo: Pendiente de pago");
        leyenda.setFont(EstilosUI.FUENTE_PEQUENA);
        leyenda.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        panelIzquierdo.add(leyenda, BorderLayout.SOUTH);

        contenedorDetalle.setBackground(EstilosUI.FONDO_TARJETA);
        contenedorDetalle.setBorder(EstilosUI.bordeTarjeta("Detalle y Atención de Mesa"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, contenedorDetalle);
        split.setDividerLocation(380);
        split.setResizeWeight(0.32);
        add(split, BorderLayout.CENTER);

        refrescar();
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
        this.mesaSeleccionada = 1;
        refrescar();
    }

    public void refrescar() {
        gridMesas.removeAll();
        for (Mesa m : restaurante.getMesas()) {
            gridMesas.add(crearTarjetaMesa(m));
        }
        gridMesas.revalidate();
        gridMesas.repaint();

        construirDetalleMesa();
    }

    private JPanel crearTarjetaMesa(Mesa mesa) {
        boolean seleccionada = mesa.getNumero() == mesaSeleccionada;
        String estado = mesa.getNombreEstado();

        Color fondo;
        Color borde;
        if ("Disponible".equals(estado)) {
            fondo = EstilosUI.EXITO_FONDO;
            borde = EstilosUI.EXITO;
        } else if ("Ocupada".equals(estado)) {
            fondo = EstilosUI.ADVERTENCIA_FONDO;
            borde = EstilosUI.ADVERTENCIA;
        } else {
            fondo = EstilosUI.PELIGRO_FONDO;
            borde = EstilosUI.PELIGRO;
        }

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(fondo);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(seleccionada ? EstilosUI.PRIMARIO : borde, seleccionada ? 3 : 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitulo = new JLabel("Mesa #" + mesa.getNumero());
        lblTitulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblTitulo.setForeground(EstilosUI.TEXTO_OSCURO);

        JLabel lblEstado = new JLabel(estado.toUpperCase());
        lblEstado.setFont(EstilosUI.FUENTE_PEQUENA);
        lblEstado.setForeground(borde.darker());

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(3));
        card.add(lblEstado);

        Visita v = mesa.getVisitaActual();
        if (v != null) {
            JLabel lblMesero = new JLabel("Mesero: " + v.getMesero().getNombre());
            lblMesero.setFont(EstilosUI.FUENTE_PEQUENA);
            JLabel lblInfo = new JLabel(v.getComensales().size() + " pers. | " + Formato.colones(v.totalMesa()));
            lblInfo.setFont(EstilosUI.FUENTE_NEGRITA);
            card.add(Box.createVerticalStrut(4));
            card.add(lblMesero);
            card.add(lblInfo);
        } else {
            JLabel lblLibre = new JLabel("Lista para recibir grupo");
            lblLibre.setFont(EstilosUI.FUENTE_PEQUENA);
            lblLibre.setForeground(EstilosUI.TEXTO_SECUNDARIO);
            card.add(Box.createVerticalStrut(4));
            card.add(lblLibre);
        }

        card.add(Box.createVerticalStrut(6));
        JButton btnVer = EstilosUI.botonSecundario(seleccionada ? "Seleccionada" : "Gestionar");
        btnVer.setEnabled(!seleccionada);
        btnVer.addActionListener(e -> {
            mesaSeleccionada = mesa.getNumero();
            refrescar();
        });
        card.add(btnVer);
        return card;
    }

    private void construirDetalleMesa() {
        contenedorDetalle.removeAll();
        Mesa mesa;
        try {
            mesa = restaurante.obtenerMesa(mesaSeleccionada);
        } catch (RestauranteException e) {
            contenedorDetalle.add(new JLabel("Seleccione una mesa válida."), BorderLayout.CENTER);
            contenedorDetalle.revalidate();
            contenedorDetalle.repaint();
            return;
        }

        String estado = mesa.getNombreEstado();
        if ("Disponible".equals(estado)) {
            contenedorDetalle.add(construirVistaAbrirVisita(mesa), BorderLayout.NORTH);
        } else if ("Ocupada".equals(estado)) {
            contenedorDetalle.add(construirVistaVisitaOcupada(mesa), BorderLayout.CENTER);
        } else {
            contenedorDetalle.add(construirVistaPendientePago(mesa), BorderLayout.CENTER);
        }

        contenedorDetalle.revalidate();
        contenedorDetalle.repaint();
    }

    private JPanel construirVistaAbrirVisita(Mesa mesa) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstilosUI.FONDO_TARJETA);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Abrir Nueva Visita en Mesa #" + mesa.getNumero());
        titulo.setFont(EstilosUI.FUENTE_TITULO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mesero responsable:"), gbc);

        JComboBox<String> cmbMesero = new JComboBox<>(new String[]{"Carlos", "Sofía", "Andrés", "Mariana"});
        cmbMesero.setEditable(true);
        cmbMesero.setFont(EstilosUI.FUENTE_NORMAL);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(cmbMesero, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Alias de comensales (separados por coma):"), gbc);

        JTextField txtAliases = new JTextField("Ana, Luis", 25);
        txtAliases.setFont(EstilosUI.FUENTE_NORMAL);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtAliases, gbc);

        JLabel nota = new JLabel("Ejemplo: Ana, Luis, Sofía (no se permiten alias repetidos ni vacíos).");
        nota.setFont(EstilosUI.FUENTE_PEQUENA);
        nota.setForeground(EstilosUI.TEXTO_SECUNDARIO);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(nota, gbc);

        JButton btnAbrir = EstilosUI.botonExito("Abrir Mesa #" + mesa.getNumero());
        btnAbrir.addActionListener(e -> {
            try {
                Object seleccionado = cmbMesero.getEditor().getItem();
                String nombreMesero = seleccionado == null ? "" : seleccionado.toString();
                Mesero mesero = new Mesero(nombreMesero);

                String raw = txtAliases.getText();
                if (raw == null || raw.isBlank()) {
                    throw new DatoInvalidoException("Debe ingresar al menos un comensal.");
                }
                List<String> aliases = new ArrayList<>();
                for (String parte : raw.split(",")) {
                    aliases.add(parte.trim());
                }
                restaurante.abrirVisita(mesa.getNumero(), mesero, aliases);
            } catch (RestauranteException ex) {
                mostrarError(ex.getMessage());
            }
        });
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        panel.add(btnAbrir, gbc);

        return panel;
    }

    private JPanel construirVistaVisitaOcupada(Mesa mesa) {
        Visita visita = mesa.getVisitaActual();
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(EstilosUI.FONDO_TARJETA);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(EstilosUI.ADVERTENCIA_FONDO);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblEnc = new JLabel("Mesa #" + mesa.getNumero() + " — OCUPADA | Mesero: " + visita.getMesero()
                + " | Inicio: " + visita.getInicio().format(HORA_FMT));
        lblEnc.setFont(EstilosUI.FUENTE_SUBTITULO);

        JLabel lblTotal = new JLabel("Total acumulado: " + Formato.colones(visita.totalMesa()));
        lblTotal.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblTotal.setForeground(EstilosUI.PRIMARIO);

        header.add(lblEnc, BorderLayout.WEST);
        header.add(lblTotal, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel panelAgregar = new JPanel(new GridLayout(2, 1, 0, 2));
        panelAgregar.setBackground(EstilosUI.FONDO_TARJETA);
        panelAgregar.setBorder(EstilosUI.bordeTarjeta("Agregar Pedido a Comensal"));

        JComboBox<Comensal> cmbComensal = new JComboBox<>(visita.getComensales().toArray(new Comensal[0]));
        JComboBox<String> cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Todas las categorías");
        for (Categoria c : restaurante.getMenu().getCategoriasActivas()) {
            cmbCategoria.addItem(c.getNombre());
        }

        JComboBox<OpcionMenu> cmbOpcion = new JComboBox<>();
        cmbOpcion.setPreferredSize(new Dimension(320, 26));
        cargarOpcionesEnCombo(cmbOpcion, "Todas las categorías");

        cmbCategoria.addActionListener(e ->
                cargarOpcionesEnCombo(cmbOpcion, (String) cmbCategoria.getSelectedItem()));

        JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, Visita.CANTIDAD_MAXIMA, 1));
        JButton btnAgregar = EstilosUI.botonPrimario("+ Agregar Pedido");
        btnAgregar.addActionListener(e -> {
            try {
                Comensal c = (Comensal) cmbComensal.getSelectedItem();
                OpcionMenu op = (OpcionMenu) cmbOpcion.getSelectedItem();
                if (c == null || op == null) {
                    mostrarError("Seleccione un comensal y una opción disponible del menú.");
                    return;
                }
                int cant = (Integer) spCantidad.getValue();
                restaurante.agregarPedido(mesa.getNumero(), c.getAlias(), op.getId(), cant);
            } catch (RestauranteException ex) {
                mostrarError(ex.getMessage());
            }
        });

        JPanel filaPedido1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        filaPedido1.setBackground(EstilosUI.FONDO_TARJETA);
        filaPedido1.add(new JLabel("Comensal:"));
        filaPedido1.add(cmbComensal);
        filaPedido1.add(new JLabel("Categoría:"));
        filaPedido1.add(cmbCategoria);

        JPanel filaPedido2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        filaPedido2.setBackground(EstilosUI.FONDO_TARJETA);
        filaPedido2.add(new JLabel("Opción:"));
        filaPedido2.add(cmbOpcion);
        filaPedido2.add(new JLabel("Cant:"));
        filaPedido2.add(spCantidad);
        filaPedido2.add(btnAgregar);

        panelAgregar.add(filaPedido1);
        panelAgregar.add(filaPedido2);

        DefaultTableModel modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Comensal", "Artículo", "Categoría", "Cant.", "Precio Unit.", "Subtotal", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (LineaPedido l : visita.getPedidos()) {
            modeloTabla.addRow(new Object[]{
                    l.getId(),
                    l.getComensal().getAlias(),
                    l.getOpcion().getNombre(),
                    l.getCategoria().getNombre(),
                    l.getCantidad(),
                    Formato.colones(l.getPrecioUnitario()),
                    Formato.colones(l.getSubtotal()),
                    l.estaAnulada() ? "ANULADO (" + l.getMotivoAnulacion() + ")" : "Activo"
            });
        }

        JTable tablaPedidos = new JTable(modeloTabla);
        EstilosUI.estilizarTabla(tablaPedidos);

        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.setBackground(EstilosUI.FONDO_TARJETA);
        centro.add(panelAgregar, BorderLayout.NORTH);
        centro.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        JPanel sur = new JPanel(new GridLayout(2, 1, 0, 4));
        sur.setBackground(EstilosUI.FONDO_TARJETA);

        StringBuilder sbSub = new StringBuilder("Subtotales por persona:  ");
        for (Comensal c : visita.getComensales()) {
            try {
                sbSub.append(c.getAlias()).append(": ")
                        .append(Formato.colones(visita.subtotalComensal(c.getAlias()))).append("   |   ");
            } catch (RestauranteException ignored) { }
        }
        JLabel lblSubtotales = new JLabel(sbSub.toString());
        lblSubtotales.setFont(EstilosUI.FUENTE_NEGRITA);

        JButton btnAnular = EstilosUI.botonPeligro("Anular Pedido Seleccionado");
        btnAnular.addActionListener(e -> {
            int fila = tablaPedidos.getSelectedRow();
            if (fila < 0) {
                mostrarError("Seleccione una fila de pedido en la tabla para anular.");
                return;
            }
            int idLinea = (Integer) modeloTabla.getValueAt(fila, 0);
            String motivo = JOptionPane.showInputDialog(this,
                    "Ingrese el motivo de anulación para el pedido #" + idLinea + ":",
                    "Anular Pedido", JOptionPane.QUESTION_MESSAGE);
            if (motivo == null) return;
            try {
                restaurante.anularPedido(mesa.getNumero(), idLinea, motivo);
            } catch (RestauranteException ex) {
                mostrarError(ex.getMessage());
            }
        });

        JButton btnResumen = EstilosUI.botonSecundario("Ver Desglose");
        btnResumen.addActionListener(e -> mostrarResumenVisita(visita));

        JPanel accionesFila1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        accionesFila1.setBackground(EstilosUI.FONDO_TARJETA);
        accionesFila1.add(btnResumen);
        accionesFila1.add(btnAnular);

        JPanel filaSur1 = new JPanel(new BorderLayout(8, 0));
        filaSur1.setBackground(EstilosUI.FONDO_TARJETA);
        filaSur1.add(lblSubtotales, BorderLayout.CENTER);
        filaSur1.add(accionesFila1, BorderLayout.EAST);

        JRadioButton rbConjunta = new JRadioButton("Cuenta Conjunta", true);
        rbConjunta.setBackground(EstilosUI.FONDO_TARJETA);
        JRadioButton rbIndividual = new JRadioButton("Pagos por Persona");
        rbIndividual.setBackground(EstilosUI.FONDO_TARJETA);
        ButtonGroup grupoModalidad = new ButtonGroup();
        grupoModalidad.add(rbConjunta);
        grupoModalidad.add(rbIndividual);

        JButton btnSolicitarCuenta = EstilosUI.botonExito("Solicitar Cuenta y Pasar a Cobro");
        btnSolicitarCuenta.addActionListener(e -> {
            ModalidadCuenta mod = rbIndividual.isSelected() ? ModalidadCuenta.INDIVIDUAL : ModalidadCuenta.CONJUNTA;
            try {
                restaurante.solicitarCuenta(mesa.getNumero(), mod);
            } catch (RestauranteException ex) {
                mostrarError(ex.getMessage());
            }
        });

        JPanel filaSur2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        filaSur2.setBackground(EstilosUI.FONDO_TARJETA);
        filaSur2.add(new JLabel("Modalidad de cobro:"));
        filaSur2.add(rbConjunta);
        filaSur2.add(rbIndividual);
        filaSur2.add(btnSolicitarCuenta);

        sur.add(filaSur1);
        sur.add(filaSur2);

        root.add(centro, BorderLayout.CENTER);
        root.add(sur, BorderLayout.SOUTH);
        return root;
    }

    private JPanel construirVistaPendientePago(Mesa mesa) {
        Visita visita = mesa.getVisitaActual();
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(EstilosUI.FONDO_TARJETA);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(EstilosUI.PELIGRO_FONDO);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblTitulo = new JLabel("Mesa #" + mesa.getNumero() + " — PENDIENTE DE PAGO (" + visita.getModalidad() + ")");
        lblTitulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblTitulo.setForeground(EstilosUI.PELIGRO.darker());

        JLabel lblSaldos = new JLabel("Total: " + Formato.colones(visita.totalMesa())
                + "  |  Pagado: " + Formato.colones(visita.totalPagado())
                + "  |  Saldo Pendiente: " + Formato.colones(visita.getSaldoPendiente()));
        lblSaldos.setFont(EstilosUI.FUENTE_SUBTITULO);

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblSaldos, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel formPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        formPago.setBackground(EstilosUI.FONDO_TARJETA);
        formPago.setBorder(EstilosUI.bordeTarjeta("Registrar Pago Ficticio"));

        boolean esIndividual = visita.getModalidad() == ModalidadCuenta.INDIVIDUAL;
        JComboBox<String> cmbPagador = new JComboBox<>();
        if (esIndividual) {
            for (Comensal c : visita.getComensales()) {
                cmbPagador.addItem(c.getAlias());
            }
        } else {
            cmbPagador.addItem("Mesa completa (Conjunta)");
            cmbPagador.setEnabled(false);
        }

        JComboBox<String> cmbMetodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "SINPE Móvil"});
        JTextField txtMonto = new JTextField(String.valueOf(visita.getSaldoPendiente()), 9);

        if (esIndividual && cmbPagador.getSelectedItem() != null) {
            try {
                txtMonto.setText(String.valueOf(visita.saldoComensal((String) cmbPagador.getSelectedItem())));
            } catch (RestauranteException ignored) { }
            cmbPagador.addActionListener(e -> {
                try {
                    String alias = (String) cmbPagador.getSelectedItem();
                    txtMonto.setText(String.valueOf(visita.saldoComensal(alias)));
                } catch (RestauranteException ignored) { }
            });
        }

        JButton btnPagar = EstilosUI.botonPrimario("Registrar Pago");
        btnPagar.addActionListener(e -> {
            try {
                int monto = Integer.parseInt(txtMonto.getText().trim());
                String alias = esIndividual ? (String) cmbPagador.getSelectedItem() : null;
                MetodoPago metodo = crearMetodoPago((String) cmbMetodo.getSelectedItem());
                restaurante.registrarPago(mesa.getNumero(), alias, monto, metodo);
            } catch (NumberFormatException nfe) {
                mostrarError("Ingrese un monto entero válido en colones (sin decimales ni símbolos).");
            } catch (RestauranteException ex) {
                mostrarError(ex.getMessage());
            }
        });

        formPago.add(new JLabel("Pagador:"));
        formPago.add(cmbPagador);
        formPago.add(new JLabel("Método:"));
        formPago.add(cmbMetodo);
        formPago.add(new JLabel("Monto (₡):"));
        formPago.add(txtMonto);
        formPago.add(btnPagar);

        DefaultTableModel modPagos = new DefaultTableModel(
                new String[]{"ID Pago", "Pagador", "Método", "Referencia", "Monto", "Fecha/Hora"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Pago p : visita.getPagos()) {
            modPagos.addRow(new Object[]{
                    p.getId(),
                    p.getAliasComensal() == null ? "Cuenta Conjunta" : p.getAliasComensal(),
                    p.getMetodo().getNombre(),
                    p.getReferencia(),
                    Formato.colones(p.getMonto()),
                    p.getFecha().format(HORA_FMT)
            });
        }
        JTable tablaPagos = new JTable(modPagos);
        EstilosUI.estilizarTabla(tablaPagos);

        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.setBackground(EstilosUI.FONDO_TARJETA);
        centro.add(formPago, BorderLayout.NORTH);
        centro.add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(8, 8));
        sur.setBackground(EstilosUI.FONDO_TARJETA);

        StringBuilder sb = new StringBuilder("Detalle por comensal:  ");
        for (Comensal c : visita.getComensales()) {
            try {
                sb.append(c.getAlias())
                        .append(" [Consumo: ").append(Formato.colones(visita.subtotalComensal(c.getAlias())));
                if (esIndividual) {
                    sb.append(", Saldo: ").append(Formato.colones(visita.saldoComensal(c.getAlias())));
                }
                sb.append("]   ");
            } catch (RestauranteException ignored) { }
        }
        JLabel lblDetalle = new JLabel(sb.toString());
        lblDetalle.setFont(EstilosUI.FUENTE_NEGRITA);
        sur.add(lblDetalle, BorderLayout.WEST);

        JButton btnResumen = EstilosUI.botonSecundario("Ver Pedidos y Desglose");
        btnResumen.addActionListener(e -> mostrarResumenVisita(visita));

        JButton btnCerrar = EstilosUI.botonExito("Cerrar Visita y Liberar Mesa");
        btnCerrar.addActionListener(e -> {
            try {
                Visita cerrada = restaurante.cerrarVisita(mesa.getNumero());
                JOptionPane.showMessageDialog(this,
                        "Visita cerrada con éxito.\nTotal cobrado: " + Formato.colones(cerrada.totalMesa())
                                + "\nLa Mesa #" + mesa.getNumero() + " vuelve a estar Disponible.",
                        "Visita Cerrada", JOptionPane.INFORMATION_MESSAGE);
            } catch (RestauranteException ex) {
                mostrarError(ex.getMessage());
            }
        });

        JPanel accionesSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        accionesSur.setBackground(EstilosUI.FONDO_TARJETA);
        accionesSur.add(btnResumen);
        accionesSur.add(btnCerrar);
        sur.add(accionesSur, BorderLayout.EAST);

        root.add(centro, BorderLayout.CENTER);
        root.add(sur, BorderLayout.SOUTH);
        return root;
    }

    private void cargarOpcionesEnCombo(JComboBox<OpcionMenu> combo, String nombreCategoria) {
        combo.removeAllItems();
        if ("Todas las categorías".equals(nombreCategoria)) {
            for (Map.Entry<Categoria, List<OpcionMenu>> entry : restaurante.getMenu().listarPorCategoria(true).entrySet()) {
                if (entry.getKey().estaActiva()) {
                    for (OpcionMenu o : entry.getValue()) {
                        combo.addItem(o);
                    }
                }
            }
        } else {
            try {
                Categoria cat = restaurante.getMenu().buscarCategoria(nombreCategoria);
                for (OpcionMenu o : restaurante.getMenu().opcionesDisponibles(cat)) {
                    combo.addItem(o);
                }
            } catch (RestauranteException ignored) { }
        }
    }

    private MetodoPago crearMetodoPago(String nombre) {
        if ("Tarjeta".equals(nombre)) return new PagoTarjeta();
        if ("SINPE Móvil".equals(nombre)) return new PagoSinpe();
        return new PagoEfectivo();
    }

    private void mostrarResumenVisita(Visita visita) {
        JTextArea area = new JTextArea(visita.resumen(), 20, 54);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Resumen y Desglose — Mesa #" + visita.getNumeroMesa(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación de Negocio", JOptionPane.WARNING_MESSAGE);
    }

}