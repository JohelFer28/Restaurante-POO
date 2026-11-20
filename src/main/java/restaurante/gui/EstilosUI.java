package restaurante.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public final class EstilosUI {
    private EstilosUI() { }

    public static final Color FONDO_APP = new Color(244, 246, 249);
    public static final Color FONDO_TARJETA = Color.WHITE;
    public static final Color BORDE_SUAVE = new Color(216, 222, 232);
    public static final Color TEXTO_OSCURO = new Color(30, 41, 59);
    public static final Color TEXTO_SECUNDARIO = new Color(100, 116, 139);

    public static final Color PRIMARIO = new Color(37, 99, 235);
    public static final Color EXITO = new Color(22, 163, 74);
    public static final Color EXITO_FONDO = new Color(220, 252, 231);
    public static final Color ADVERTENCIA = new Color(217, 119, 6);
    public static final Color ADVERTENCIA_FONDO = new Color(254, 243, 199);
    public static final Color PELIGRO = new Color(220, 38, 38);
    public static final Color PELIGRO_FONDO = new Color(254, 226, 226);

    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_NEGRITA = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FUENTE_PEQUENA = new Font("SansSerif", Font.PLAIN, 12);

    public static Border bordeTarjeta(String titulo) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDE_SUAVE),
                        titulo,
                        0, 0, FUENTE_SUBTITULO, TEXTO_OSCURO),
                BorderFactory.createEmptyBorder(8, 10, 10, 10)
        );
    }

    public static JButton botonPrimario(String texto) {
        return crearBoton(texto, PRIMARIO, Color.WHITE);
    }

    public static JButton botonExito(String texto) {
        return crearBoton(texto, EXITO, Color.WHITE);
    }

    public static JButton botonPeligro(String texto) {
        return crearBoton(texto, PELIGRO, Color.WHITE);
    }

    public static JButton botonSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(FUENTE_NEGRITA);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JButton crearBoton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setFont(FUENTE_NEGRITA);
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(FUENTE_NEGRITA);
        tabla.setFillsViewportHeight(true);
    }

    public static JPanel crearTarjetaKpi(String titulo, JLabel lblValor, Color acento) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(FONDO_TARJETA);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, acento),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE_SUAVE),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                )
        ));
        JLabel t = new JLabel(titulo);
        t.setFont(FUENTE_PEQUENA);
        t.setForeground(TEXTO_SECUNDARIO);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblValor.setForeground(TEXTO_OSCURO);
        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(lblValor);
        return p;
    }
}
