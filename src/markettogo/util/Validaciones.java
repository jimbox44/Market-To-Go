package markettogo.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Utilidades de validacion y estilos para Market-To-Go.
 */
public class Validaciones {

    // ── Colores de la aplicacion ───────────────────────────
    public static final Color AZUL_PRIMARIO  = new Color(44, 95, 138);
    public static final Color AZUL_OSCURO    = new Color(26, 62, 92);
    public static final Color BLANCO         = Color.WHITE;
    public static final Color GRIS_FONDO     = new Color(245, 248, 252);
    public static final Color GRIS_BORDE     = new Color(204, 204, 204);
    public static final Color VERDE_EXITO    = new Color(40, 167, 69);
    public static final Color ROJO_ERROR     = new Color(220, 53, 69);
    public static final Color NARANJA_ALERTA = new Color(255, 153, 0);

    // ── Fuentes ────────────────────────────────────────────
    public static final Font FUENTE_TITULO   = new Font("Arial", Font.BOLD,  20);
    public static final Font FUENTE_SUBTITULO= new Font("Arial", Font.BOLD,  14);
    public static final Font FUENTE_NORMAL   = new Font("Arial", Font.PLAIN, 12);
    public static final Font FUENTE_PEQUENA  = new Font("Arial", Font.PLAIN, 11);

    // ── Validacion ─────────────────────────────────────────
    public static boolean emailValido(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(regex, email.trim());
    }

    public static boolean campoVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean precioValido(String valor) {
        try {
            double d = Double.parseDouble(valor.replace(",", "."));
            return d > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean contrasenaSuficiente(String pass) {
        return pass != null && pass.length() >= 6;
    }

    // ── Estilos de botones ─────────────────────────────────
    public static JButton botonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(AZUL_PRIMARIO);
        btn.setForeground(BLANCO);
        btn.setFont(FUENTE_SUBTITULO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 36));
        return btn;
    }

    public static JButton botonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(GRIS_FONDO);
        btn.setForeground(AZUL_PRIMARIO);
        btn.setFont(FUENTE_SUBTITULO);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 36));
        return btn;
    }

    public static JButton botonPeligro(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(ROJO_ERROR);
        btn.setForeground(BLANCO);
        btn.setFont(FUENTE_SUBTITULO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Aplica un renderer propio al encabezado de la tabla.
     * Necesario porque el Look&Feel de Windows ignora
     * JTableHeader.setBackground/setForeground al dibujar,
     * dejando el texto blanco sobre fondo blanco (invisible).
     */
    public static void estilizarHeader(JTable tabla) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setOpaque(true);
        renderer.setBackground(AZUL_PRIMARIO);
        renderer.setForeground(BLANCO);
        renderer.setFont(FUENTE_SUBTITULO);
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        renderer.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        tabla.getTableHeader().setDefaultRenderer(renderer);
    }

    // ── Dialogs ────────────────────────────────────────────
    public static void mostrarError(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarExito(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Exito", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component parent, String mensaje) {
        int res = JOptionPane.showConfirmDialog(parent, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    // ── Estilo de panel de encabezado ──────────────────────
    public static JPanel panelEncabezado(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new BorderLayout(5, 2));
        panel.setBackground(AZUL_PRIMARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(BLANCO);

        panel.add(lblTitulo, BorderLayout.NORTH);

        if (subtitulo != null && !subtitulo.isEmpty()) {
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(FUENTE_NORMAL);
            lblSub.setForeground(new Color(180, 210, 240));
            panel.add(lblSub, BorderLayout.CENTER);
        }
        return panel;
    }
}
