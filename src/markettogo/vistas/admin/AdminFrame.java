package markettogo.vistas.admin;

import markettogo.dao.*;
import markettogo.modelo.*;
import markettogo.util.Validaciones;
import markettogo.vistas.auth.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel de administracion con pestanas para gestionar el sistema.
 */
public class AdminFrame extends JFrame {

    private final Usuario admin;

    // DAOs
    private final UsuarioDAO     usuarioDAO     = new UsuarioDAO();
    private final ArticuloDAO    articuloDAO    = new ArticuloDAO();
    private final PedidoDAO      pedidoDAO      = new PedidoDAO();
    private final TransaccionDAO txDAO          = new TransaccionDAO();
    private final CategoriaDAO   categoriaDAO   = new CategoriaDAO();

    // Tabs y modelos
    private JTabbedPane tabs;

    // -- Usuarios --
    private DefaultTableModel modeloUsuarios;
    private JTable tblUsuarios;
    private JButton btnDesactivarUser, btnRefreshUsers;

    // -- Pedidos --
    private DefaultTableModel modeloPedidos;
    private JTable tblPedidos;
    private JButton btnAsignarRepartidor, btnRefreshPedidos;

    // -- Transacciones --
    private DefaultTableModel modeloTx;
    private JTable tblTx;
    private JButton btnRefreshTx;

    // -- Categorias --
    private DefaultTableModel modeloCats;
    private JTable tblCats;
    private JButton btnNuevaCat, btnRefreshCats;

    public AdminFrame(Usuario admin) {
        this.admin = admin;
        initComponents();
        cargarTodo();
    }

    private void initComponents() {
        setTitle("Market-To-Go — Panel Administrador");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1050, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        // Encabezado
        JPanel header = Validaciones.panelEncabezado(
            "Panel de Administracion",
            "Bienvenido, " + admin.getNombreCompleto() + " — " + admin.getDescripcionRol());
        JButton btnSalir = Validaciones.botonPeligro("Cerrar sesion");
        btnSalir.setPreferredSize(new Dimension(140, 30));
        header.add(btnSalir, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        btnSalir.addActionListener(e -> salir());

        // Tabs
        tabs = new JTabbedPane();
        tabs.setFont(Validaciones.FUENTE_SUBTITULO);
        tabs.setBackground(Validaciones.GRIS_FONDO);
        tabs.addTab("Usuarios",      buildTabUsuarios());
        tabs.addTab("Pedidos",       buildTabPedidos());
        tabs.addTab("Transacciones", buildTabTransacciones());
        tabs.addTab("Categorias",    buildTabCategorias());
        tabs.addTab("Resumen",       buildTabResumen());
        add(tabs, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });
    }

    // ── TAB USUARIOS ──────────────────────────────────────
    private JPanel buildTabUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);

        String[] cols = {"ID", "Nombre", "Apellido", "Email", "Rol", "Activo", "Registro"};
        modeloUsuarios = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsuarios = buildTabla(modeloUsuarios);
        panel.add(new JScrollPane(tblUsuarios), BorderLayout.CENTER);

        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bots.setBackground(Validaciones.GRIS_FONDO);
        btnRefreshUsers    = Validaciones.botonSecundario("Refrescar");
        btnDesactivarUser  = Validaciones.botonPeligro("Desactivar usuario");
        bots.add(btnRefreshUsers);
        bots.add(btnDesactivarUser);
        panel.add(bots, BorderLayout.SOUTH);

        btnRefreshUsers  .addActionListener(e -> cargarUsuarios());
        btnDesactivarUser.addActionListener(e -> desactivarUsuario());
        return panel;
    }

    // ── TAB PEDIDOS ───────────────────────────────────────
    private JPanel buildTabPedidos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);

        String[] cols = {"ID", "Articulo", "Comprador", "Estado", "Repartidor", "Direccion", "Fecha"};
        modeloPedidos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPedidos = buildTabla(modeloPedidos);
        panel.add(new JScrollPane(tblPedidos), BorderLayout.CENTER);

        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bots.setBackground(Validaciones.GRIS_FONDO);
        btnRefreshPedidos     = Validaciones.botonSecundario("Refrescar");
        btnAsignarRepartidor  = Validaciones.botonPrimario("Asignar repartidor");
        bots.add(btnRefreshPedidos);
        bots.add(btnAsignarRepartidor);
        panel.add(bots, BorderLayout.SOUTH);

        btnRefreshPedidos   .addActionListener(e -> cargarPedidos());
        btnAsignarRepartidor.addActionListener(e -> asignarRepartidor());
        return panel;
    }

    // ── TAB TRANSACCIONES ─────────────────────────────────
    private JPanel buildTabTransacciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);

        String[] cols = {"ID", "Referencia", "Comprador", "Vendedor", "Monto", "Comision 5%", "Al vendedor", "Estado", "Fecha"};
        modeloTx = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTx = buildTabla(modeloTx);
        panel.add(new JScrollPane(tblTx), BorderLayout.CENTER);

        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bots.setBackground(Validaciones.GRIS_FONDO);
        btnRefreshTx = Validaciones.botonSecundario("Refrescar");
        bots.add(btnRefreshTx);
        panel.add(bots, BorderLayout.SOUTH);

        btnRefreshTx.addActionListener(e -> cargarTransacciones());
        return panel;
    }

    // ── TAB CATEGORIAS ────────────────────────────────────
    private JPanel buildTabCategorias() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);

        String[] cols = {"ID", "Nombre", "Descripcion", "Activa"};
        modeloCats = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCats = buildTabla(modeloCats);
        panel.add(new JScrollPane(tblCats), BorderLayout.CENTER);

        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bots.setBackground(Validaciones.GRIS_FONDO);
        btnRefreshCats = Validaciones.botonSecundario("Refrescar");
        btnNuevaCat    = Validaciones.botonPrimario("Nueva categoria");
        bots.add(btnRefreshCats);
        bots.add(btnNuevaCat);
        panel.add(bots, BorderLayout.SOUTH);

        btnRefreshCats.addActionListener(e -> cargarCategorias());
        btnNuevaCat   .addActionListener(e -> nuevaCategoria());
        return panel;
    }

    // ── TAB RESUMEN ───────────────────────────────────────
    private JPanel buildTabResumen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = 1.0;

        String[][] metricas = {
            {"Total usuarios",       String.valueOf(usuarioDAO.listarTodos().size())},
            {"Pedidos totales",      String.valueOf(pedidoDAO.listarTodos().size())},
            {"Transacciones",        String.valueOf(txDAO.listarTodas().size())},
            {"Comisiones cobradas",  calcularComisionTotal()},
            {"Categoria mas activa", calcularCategoriaMasActiva()},
        };

        int col = 0;
        for (String[] m : metricas) {
            JPanel card = crearCard(m[0], m[1]);
            gbc.gridx = col % 2;
            gbc.gridy = col / 2;
            panel.add(card, gbc);
            col++;
        }
        return panel;
    }

    private JPanel crearCard(String titulo, String valor) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Validaciones.GRIS_BORDE),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(Validaciones.FUENTE_NORMAL);
        lblTit.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Arial", Font.BOLD, 28));
        lblVal.setForeground(Validaciones.AZUL_PRIMARIO);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }

    private String calcularComisionTotal() {
        double total = txDAO.listarTodas().stream()
            .mapToDouble(Transaccion::getComision).sum();
        return "$" + String.format("%.2f", total);
    }

    /** Agrupa los articulos publicados por categoria para hallar la mas activa. */
    private String calcularCategoriaMasActiva() {
        Map<String, Long> porCategoria = articuloDAO.listarTodos().stream()
            .collect(Collectors.groupingBy(Articulo::getNombreCategoria, Collectors.counting()));
        return porCategoria.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey() + " (" + e.getValue() + ")")
            .orElse("N/A");
    }

    // ── Helpers ───────────────────────────────────────────
    private JTable buildTabla(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(26);
        t.setFont(Validaciones.FUENTE_NORMAL);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Validaciones.estilizarHeader(t);
        t.setGridColor(Validaciones.GRIS_BORDE);
        // Ocultar columna ID
        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        return t;
    }

    private void cargarTodo() {
        cargarUsuarios();
        cargarPedidos();
        cargarTransacciones();
        cargarCategorias();
    }

    private void cargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        usuarioDAO.listarTodos().forEach(u ->
            modeloUsuarios.addRow(new Object[]{
                u.getId(), u.getNombre(), u.getApellido(),
                u.getEmail(), u.getRol(), u.isActivo() ? "Si" : "No",
                u.getFechaRegistro()
            })
        );
    }

    private void cargarPedidos() {
        modeloPedidos.setRowCount(0);
        pedidoDAO.listarTodos().forEach(p ->
            modeloPedidos.addRow(new Object[]{
                p.getId(), p.getTituloArticulo(), p.getNombreComprador(),
                p.getEstadoPedido(),
                p.getNombreRepartidor() != null ? p.getNombreRepartidor() : "Sin asignar",
                p.getDireccionEntrega(), p.getFechaPedido()
            })
        );
    }

    private void cargarTransacciones() {
        modeloTx.setRowCount(0);
        txDAO.listarTodas().forEach(t ->
            modeloTx.addRow(new Object[]{
                t.getId(), t.getReferencia(), t.getNombreComprador(), t.getNombreVendedor(),
                "$" + String.format("%.2f", t.getMonto()),
                "$" + String.format("%.2f", t.getComision()),
                "$" + String.format("%.2f", t.getMontoVendedor()),
                t.getEstado(), t.getFecha()
            })
        );
    }

    private void cargarCategorias() {
        modeloCats.setRowCount(0);
        categoriaDAO.listarTodas().forEach(c ->
            modeloCats.addRow(new Object[]{
                c.getId(), c.getNombre(), c.getDescripcion(), c.isActiva() ? "Si" : "No"
            })
        );
    }

    private void desactivarUsuario() {
        int fila = tblUsuarios.getSelectedRow();
        if (fila < 0) { Validaciones.mostrarError(this, "Selecciona un usuario."); return; }
        int uid = (int) modeloUsuarios.getValueAt(fila, 0);
        if (uid == admin.getId()) {
            Validaciones.mostrarError(this, "No puedes desactivar tu propia cuenta.");
            return;
        }
        if (Validaciones.confirmar(this, "Desactivar este usuario?")) {
            usuarioDAO.desactivar(uid);
            cargarUsuarios();
            Validaciones.mostrarExito(this, "Usuario desactivado.");
        }
    }

    private void asignarRepartidor() {
        int fila = tblPedidos.getSelectedRow();
        if (fila < 0) { Validaciones.mostrarError(this, "Selecciona un pedido."); return; }
        int pedidoId = (int) modeloPedidos.getValueAt(fila, 0);

        List<Usuario> repartidores = usuarioDAO.listarRepartidores();
        if (repartidores.isEmpty()) {
            Validaciones.mostrarError(this, "No hay repartidores registrados.");
            return;
        }
        Usuario[] arr = repartidores.toArray(new Usuario[0]);
        Usuario elegido = (Usuario) JOptionPane.showInputDialog(this,
            "Selecciona el repartidor:", "Asignar repartidor",
            JOptionPane.QUESTION_MESSAGE, null, arr, arr[0]);
        if (elegido == null) return;

        if (pedidoDAO.asignarRepartidor(pedidoId, elegido.getId())) {
            Validaciones.mostrarExito(this, "Repartidor asignado: " + elegido.getNombreCompleto());
            cargarPedidos();
        } else {
            Validaciones.mostrarError(this, "Error al asignar repartidor.");
        }
    }

    private void nuevaCategoria() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva categoria:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        String desc = JOptionPane.showInputDialog(this, "Descripcion (opcional):");

        Categoria c = new Categoria(0, nombre.trim(), desc, true);
        if (categoriaDAO.insertar(c)) {
            Validaciones.mostrarExito(this, "Categoria creada.");
            cargarCategorias();
        } else {
            Validaciones.mostrarError(this, "Error al crear la categoria.");
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
