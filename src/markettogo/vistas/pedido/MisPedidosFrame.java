package markettogo.vistas.pedido;

import markettogo.dao.PedidoDAO;
import markettogo.dao.TransaccionDAO;
import markettogo.modelo.Pedido;
import markettogo.modelo.Transaccion;
import markettogo.modelo.Usuario;
import markettogo.util.GenerarComprobante;
import markettogo.util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Historial de pedidos del comprador.
 */
public class MisPedidosFrame extends JFrame {

    private final Usuario usuario;
    private JTable tblPedidos;
    private DefaultTableModel modelo;
    private JButton btnComprobante, btnRefrescar;

    private final PedidoDAO      pedidoDAO = new PedidoDAO();
    private final TransaccionDAO txDAO     = new TransaccionDAO();

    public MisPedidosFrame(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        cargar();
    }

    private void initComponents() {
        setTitle("Mis pedidos");
        setSize(820, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        add(Validaciones.panelEncabezado("Mis pedidos", "Historial de compras realizadas"), BorderLayout.NORTH);

        String[] cols = {"ID", "Articulo", "Monto", "Estado", "Direccion", "Repartidor", "Fecha"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPedidos = new JTable(modelo);
        tblPedidos.setRowHeight(26);
        tblPedidos.setFont(Validaciones.FUENTE_NORMAL);
        Validaciones.estilizarHeader(tblPedidos);
        tblPedidos.getColumnModel().getColumn(0).setMinWidth(0);
        tblPedidos.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tblPedidos);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.setBackground(Validaciones.GRIS_FONDO);
        btnRefrescar   = Validaciones.botonSecundario("Refrescar");
        btnComprobante = Validaciones.botonPrimario("Ver comprobante");
        botones.add(btnRefrescar);
        botones.add(btnComprobante);
        add(botones, BorderLayout.SOUTH);

        btnRefrescar  .addActionListener(e -> cargar());
        btnComprobante.addActionListener(e -> generarComprobante());
    }

    private void cargar() {
        modelo.setRowCount(0);
        pedidoDAO.listarPorComprador(usuario.getId()).forEach(p -> {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getTituloArticulo(),
                "$" + String.format("%.2f", p.getPrecioArticulo()),
                p.getEstadoPedido(),
                p.getDireccionEntrega(),
                p.getNombreRepartidor() != null ? p.getNombreRepartidor() : "Sin asignar",
                p.getFechaPedido()
            });
        });
    }

    private void generarComprobante() {
        int fila = tblPedidos.getSelectedRow();
        if (fila < 0) { Validaciones.mostrarError(this, "Selecciona un pedido."); return; }
        int pedidoId = (int) modelo.getValueAt(fila, 0);

        Pedido pedido = pedidoDAO.listarPorComprador(usuario.getId())
            .stream().filter(p -> p.getId() == pedidoId).findFirst().orElse(null);
        Transaccion tx = txDAO.buscarPorPedido(pedidoId);

        if (tx == null) {
            Validaciones.mostrarError(this, "No se encontro transaccion para este pedido.");
            return;
        }

        String ruta = GenerarComprobante.generar(tx, pedido);
        if (ruta != null) {
            if (Validaciones.confirmar(this, "Comprobante generado en:\n" + ruta + "\n\nAbrir ahora?")) {
                GenerarComprobante.abrirComprobante(ruta);
            }
        } else {
            Validaciones.mostrarError(this, "No se pudo generar el comprobante.");
        }
    }
}
