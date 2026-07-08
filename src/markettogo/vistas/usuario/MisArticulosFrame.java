package markettogo.vistas.usuario;

import markettogo.dao.ArticuloDAO;
import markettogo.modelo.Articulo;
import markettogo.modelo.Usuario;
import markettogo.util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Muestra los articulos publicados por el usuario actual.
 */
public class MisArticulosFrame extends JFrame {

    private final Usuario usuario;
    private JTable tblArticulos;
    private DefaultTableModel modelo;
    private JButton btnDesactivar, btnEditar, btnRefrescar;

    private final ArticuloDAO articuloDAO = new ArticuloDAO();

    public MisArticulosFrame(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        cargar();
    }

    private void initComponents() {
        setTitle("Mis articulos");
        setSize(780, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        add(Validaciones.panelEncabezado("Mis articulos publicados", "Administra tus publicaciones"), BorderLayout.NORTH);

        String[] cols = {"ID", "Titulo", "Precio", "Categoria", "Estado", "Trueque"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblArticulos = new JTable(modelo);
        tblArticulos.setRowHeight(26);
        tblArticulos.setFont(Validaciones.FUENTE_NORMAL);
        Validaciones.estilizarHeader(tblArticulos);
        tblArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tblArticulos.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tblArticulos);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.setBackground(Validaciones.GRIS_FONDO);
        btnRefrescar  = Validaciones.botonSecundario("Refrescar");
        btnEditar     = Validaciones.botonSecundario("Editar");
        btnDesactivar = Validaciones.botonPeligro("Desactivar");
        botones.add(btnRefrescar);
        botones.add(btnEditar);
        botones.add(btnDesactivar);
        add(botones, BorderLayout.SOUTH);

        btnRefrescar .addActionListener(e -> cargar());
        btnDesactivar.addActionListener(e -> desactivar());
    }

    private void cargar() {
        modelo.setRowCount(0);
        articuloDAO.listarPorVendedor(usuario.getId()).forEach(a ->
            modelo.addRow(new Object[]{
                a.getId(), a.getTitulo(), "$" + a.getPrecioFormateado(),
                a.getNombreCategoria(), a.getEstado(),
                a.isDisponibleTrueque() ? "Si" : "No"
            })
        );
    }

    private void desactivar() {
        int fila = tblArticulos.getSelectedRow();
        if (fila < 0) { Validaciones.mostrarError(this, "Selecciona un articulo."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        if (Validaciones.confirmar(this, "Desactivar este articulo?")) {
            articuloDAO.cambiarEstado(id, Articulo.Estado.INACTIVO);
            cargar();
        }
    }
}
