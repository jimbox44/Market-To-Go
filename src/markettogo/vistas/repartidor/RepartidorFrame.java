package markettogo.vistas.repartidor;

import markettogo.dao.NotificacionDAO;
import markettogo.dao.PedidoDAO;
import markettogo.modelo.Notificacion;
import markettogo.modelo.Pedido;
import markettogo.modelo.Usuario;
import markettogo.util.Validaciones;
import markettogo.vistas.auth.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Panel del repartidor — ve sus pedidos asignados y actualiza estados.
 */
public class RepartidorFrame extends JFrame {

    private final Usuario usuario;
    private JTable tblPedidos;
    private DefaultTableModel modelo;
    private JButton btnEnCamino, btnEntregado, btnRefrescar, btnSalir;

    private final PedidoDAO     pedidoDAO = new PedidoDAO();
    private final NotificacionDAO notifDAO = new NotificacionDAO();

    public RepartidorFrame(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        cargar();
    }

    private void initComponents() {
        setTitle("Market-To-Go — Panel Repartidor");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        add(Validaciones.panelEncabezado(
            "Panel de Repartidor — " + usuario.getNombreCompleto(),
            usuario.getDescripcionRol() + " — Gestiona tus entregas asignadas"), BorderLayout.NORTH);

        String[] cols = {"ID", "Articulo", "Monto", "Comprador", "Direccion", "Estado", "Notas", "Fecha"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPedidos = new JTable(modelo);
        tblPedidos.setRowHeight(28);
        tblPedidos.setFont(Validaciones.FUENTE_NORMAL);
        tblPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Validaciones.estilizarHeader(tblPedidos);
        tblPedidos.getColumnModel().getColumn(0).setMinWidth(0);
        tblPedidos.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tblPedidos);
        scroll.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.setBackground(Validaciones.GRIS_FONDO);
        btnSalir     = Validaciones.botonPeligro("Cerrar sesion");
        btnRefrescar = Validaciones.botonSecundario("Refrescar");
        btnEnCamino  = Validaciones.botonPrimario("Marcar EN CAMINO");
        btnEntregado = Validaciones.botonPrimario("Marcar ENTREGADO");
        btnEntregado.setBackground(Validaciones.VERDE_EXITO);

        botones.add(btnSalir);
        botones.add(btnRefrescar);
        botones.add(btnEnCamino);
        botones.add(btnEntregado);
        add(botones, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargar());
        btnEnCamino .addActionListener(e -> cambiarEstado(Pedido.Estado.EN_CAMINO));
        btnEntregado.addActionListener(e -> cambiarEstado(Pedido.Estado.ENTREGADO));
        btnSalir    .addActionListener(e -> salir());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });
    }

    private void cargar() {
        modelo.setRowCount(0);
        pedidoDAO.listarPorRepartidor(usuario.getId()).forEach(p ->
            modelo.addRow(new Object[]{
                p.getId(),
                p.getTituloArticulo(),
                "$" + String.format("%.2f", p.getPrecioArticulo()),
                p.getNombreComprador(),
                p.getDireccionEntrega(),
                p.getEstadoPedido(),
                p.getNotas() != null ? p.getNotas() : "",
                p.getFechaPedido()
            })
        );
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No tienes pedidos asignados en este momento.",
                "Sin pedidos", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cambiarEstado(Pedido.Estado nuevoEstado) {
        int fila = tblPedidos.getSelectedRow();
        if (fila < 0) { Validaciones.mostrarError(this, "Selecciona un pedido."); return; }
        int pedidoId = (int) modelo.getValueAt(fila, 0);
        int compradorId = pedidoDAO.listarPorRepartidor(usuario.getId())
            .stream().filter(p -> p.getId() == pedidoId)
            .findFirst().map(Pedido::getCompradorId).orElse(0);

        if (pedidoDAO.actualizarEstado(pedidoId, nuevoEstado)) {
            // Notificar al comprador
            String msg = nuevoEstado == Pedido.Estado.EN_CAMINO
                ? "Tu pedido esta en camino. Pronto llegara a tu direccion."
                : "Tu pedido ha sido entregado. Que lo disfrutes!";
            notifDAO.insertar(new Notificacion(compradorId,
                "Actualizacion de pedido #" + pedidoId, msg, Notificacion.Tipo.PEDIDO));

            Validaciones.mostrarExito(this, "Estado actualizado a: " + nuevoEstado);
            cargar();
        } else {
            Validaciones.mostrarError(this, "Error al actualizar el estado.");
        }
    }

    private void salir() {
        if (Validaciones.confirmar(this, "Deseas cerrar sesion?")) {
            markettogo.util.SesionManager.limpiar();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
