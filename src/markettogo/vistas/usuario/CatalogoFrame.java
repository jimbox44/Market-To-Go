package markettogo.vistas.usuario;

import markettogo.dao.*;
import markettogo.excepciones.ArticuloNoDisponibleException;
import markettogo.excepciones.OperacionNoAutorizadaException;
import markettogo.modelo.*;
import markettogo.util.SesionManager;
import markettogo.util.Validaciones;
import markettogo.vistas.auth.LoginFrame;
import markettogo.vistas.trueque.TruequeFrame;
import markettogo.vistas.pedido.MisPedidosFrame;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Pantalla principal del comprador/vendedor.
 * Muestra el catalogo de articulos con opciones de busqueda, compra y trueque.
 */
public class CatalogoFrame extends JFrame {

    private final Usuario usuarioActual;

    private JTextField    txtBuscar;
    private JComboBox<Categoria> cbCategoria;
    private JTable        tblArticulos;
    private DefaultTableModel modeloTabla;
    private JButton       btnBuscar, btnPublicar, btnComprar, btnTrueque,
                          btnMisPedidos, btnMisArticulos, btnNotificaciones, btnSalir;
    private JLabel        lblBienvenida;

    private final ArticuloDAO  articuloDAO  = new ArticuloDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final PedidoDAO    pedidoDAO    = new PedidoDAO();
    private final TransaccionDAO txDAO       = new TransaccionDAO();
    private final NotificacionDAO notifDAO   = new NotificacionDAO();

    private volatile boolean hiloActivo = true;
    private Thread hiloNotificaciones;

    public CatalogoFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
        cargarCategorias();
        cargarArticulos();
        iniciarHiloNotificaciones();
    }

    /**
     * Hilo en segundo plano que consulta periodicamente las notificaciones
     * no leidas y actualiza el boton correspondiente sin bloquear la UI.
     */
    private void iniciarHiloNotificaciones() {
        hiloNotificaciones = new Thread(() -> {
            while (hiloActivo) {
                long noLeidas = notifDAO.listarPorUsuario(usuarioActual.getId())
                    .stream().filter(n -> !n.isLeida()).count();
                String texto = noLeidas > 0 ? "Notif. (" + noLeidas + ")" : "Notif.";
                SwingUtilities.invokeLater(() -> btnNotificaciones.setText(texto));
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Hilo-Notificaciones");
        hiloNotificaciones.setDaemon(true);
        hiloNotificaciones.start();
    }

    private void initComponents() {
        setTitle("Market-To-Go — Catalogo");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(980, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        // ── Barra superior ──
        JPanel topBar = new JPanel(new BorderLayout(10, 5));
        topBar.setBackground(Validaciones.AZUL_PRIMARIO);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        lblBienvenida = new JLabel("Hola, " + usuarioActual.getNombre() + "!");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setToolTipText("<html><b>" + usuarioActual.getDescripcionRol() + "</b><br>" +
            String.join("<br>", usuarioActual.getPermisos()) + "</html>");

        // Barra de busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelBusqueda.setOpaque(false);
        txtBuscar   = new JTextField(20);
        txtBuscar.setFont(Validaciones.FUENTE_NORMAL);
        cbCategoria = new JComboBox<>();
        cbCategoria.setFont(Validaciones.FUENTE_NORMAL);
        cbCategoria.setPreferredSize(new Dimension(140, 28));
        btnBuscar   = Validaciones.botonSecundario("Buscar");
        btnBuscar.setPreferredSize(new Dimension(90, 28));
        panelBusqueda.add(new JLabel("<html><font color='white'>Buscar:</font></html>"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(new JLabel("<html><font color='white'>Categoria:</font></html>"));
        panelBusqueda.add(cbCategoria);
        panelBusqueda.add(btnBuscar);

        // Botones de sesion
        JPanel panelSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelSesion.setOpaque(false);
        btnNotificaciones = Validaciones.botonSecundario("Notif.");
        btnNotificaciones.setPreferredSize(new Dimension(70, 28));
        btnSalir = Validaciones.botonPeligro("Salir");
        btnSalir.setPreferredSize(new Dimension(70, 28));
        panelSesion.add(btnNotificaciones);
        panelSesion.add(btnSalir);

        topBar.add(lblBienvenida, BorderLayout.WEST);
        topBar.add(panelBusqueda, BorderLayout.CENTER);
        topBar.add(panelSesion,   BorderLayout.EAST);

        // ── Barra lateral de acciones ──
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(Validaciones.AZUL_OSCURO);
        sideBar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sideBar.setPreferredSize(new Dimension(160, 0));

        JLabel lblMenu = new JLabel("  Mi cuenta");
        lblMenu.setFont(Validaciones.FUENTE_SUBTITULO);
        lblMenu.setForeground(new Color(160, 200, 240));
        sideBar.add(lblMenu);
        sideBar.add(Box.createVerticalStrut(12));

        btnPublicar   = crearBotonMenu("Publicar articulo");
        btnMisArticulos = crearBotonMenu("Mis articulos");
        btnMisPedidos = crearBotonMenu("Mis pedidos");
        btnTrueque    = crearBotonMenu("Trueques");

        sideBar.add(btnPublicar);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(btnMisArticulos);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(btnMisPedidos);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(btnTrueque);

        // ── Tabla de articulos ──
        String[] cols = {"ID", "Titulo", "Categoria", "Precio", "Ubicacion", "Vendedor", "Trueque", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblArticulos = new JTable(modeloTabla);
        tblArticulos.setFont(Validaciones.FUENTE_NORMAL);
        tblArticulos.setRowHeight(28);
        tblArticulos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Validaciones.estilizarHeader(tblArticulos);
        tblArticulos.setGridColor(Validaciones.GRIS_BORDE);

        // Ocultar columna ID
        tblArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tblArticulos.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tblArticulos);
        scroll.setBorder(BorderFactory.createLineBorder(Validaciones.GRIS_BORDE));

        // ── Panel de acciones de articulo ──
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panelAcciones.setBackground(Validaciones.GRIS_FONDO);
        panelAcciones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Validaciones.GRIS_BORDE));
        btnComprar = Validaciones.botonPrimario("Comprar");
        btnComprar.setPreferredSize(new Dimension(120, 34));
        JButton btnVerDetalle = Validaciones.botonSecundario("Ver detalle");
        btnVerDetalle.setPreferredSize(new Dimension(120, 34));
        panelAcciones.add(btnVerDetalle);
        panelAcciones.add(btnComprar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Validaciones.GRIS_FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        centro.add(scroll, BorderLayout.CENTER);
        centro.add(panelAcciones, BorderLayout.SOUTH);

        add(topBar,  BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(centro,  BorderLayout.CENTER);

        // ── Acciones ──
        btnBuscar      .addActionListener(e -> buscar());
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) buscar();
            }
        });
        cbCategoria    .addActionListener(e -> filtrarPorCategoria());
        btnComprar     .addActionListener(e -> comprar());
        btnVerDetalle  .addActionListener(e -> verDetalle());
        btnPublicar    .addActionListener(e -> publicarArticulo());
        btnMisArticulos.addActionListener(e -> verMisArticulos());
        btnMisPedidos  .addActionListener(e -> verMisPedidos());
        btnTrueque     .addActionListener(e -> verTrueques());
        btnNotificaciones.addActionListener(e -> verNotificaciones());
        btnSalir       .addActionListener(e -> salir());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(Validaciones.FUENTE_NORMAL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(Validaciones.AZUL_OSCURO);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(140, 32));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void cargarCategorias() {
        cbCategoria.removeAllItems();
        cbCategoria.addItem(new Categoria(0, "-- Todas --", "", true));
        categoriaDAO.listarActivas().forEach(c -> cbCategoria.addItem(c));
    }

    private void cargarArticulos() {
        llenarTabla(articuloDAO.listarDisponibles());
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarArticulos();
        } else {
            llenarTabla(articuloDAO.buscarPorTitulo(texto));
        }
    }

    private void filtrarPorCategoria() {
        Categoria cat = (Categoria) cbCategoria.getSelectedItem();
        if (cat == null || cat.getId() == 0) {
            cargarArticulos();
        } else {
            llenarTabla(articuloDAO.listarPorCategoria(cat.getId()));
        }
    }

    private void llenarTabla(List<Articulo> lista) {
        modeloTabla.setRowCount(0);
        for (Articulo a : lista) {
            if (a.getVendedorId() == usuarioActual.getId()) continue; // no mostrar los propios
            modeloTabla.addRow(new Object[]{
                a.getId(),
                a.getTitulo(),
                a.getNombreCategoria(),
                "$" + a.getPrecioFormateado(),
                a.getUbicacion(),
                a.getNombreVendedor(),
                a.isDisponibleTrueque() ? "Si" : "No",
                a.getEstado()
            });
        }
    }

    private Articulo getArticuloSeleccionado() {
        int fila = tblArticulos.getSelectedRow();
        if (fila < 0) {
            Validaciones.mostrarError(this, "Selecciona un articulo de la lista.");
            return null;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return articuloDAO.buscarPorId(id);
    }

    private void verDetalle() {
        Articulo a = getArticuloSeleccionado();
        if (a == null) return;
        String info = "<html><b>" + a.getTitulo() + "</b><br><br>" +
                      "Precio: $" + a.getPrecioFormateado() + "<br>" +
                      "Categoria: " + a.getNombreCategoria() + "<br>" +
                      "Ubicacion: " + a.getUbicacion() + "<br>" +
                      "Vendedor: "  + a.getNombreVendedor() + "<br>" +
                      "Acepta trueque: " + (a.isDisponibleTrueque() ? "Si" : "No") + "<br><br>" +
                      "Descripcion:<br>" + a.getDescripcion() + "</html>";
        JOptionPane.showMessageDialog(this, info, "Detalle del articulo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void comprar() {
        Articulo a = getArticuloSeleccionado();
        if (a == null) return;

        String dir = JOptionPane.showInputDialog(this,
            "Direccion de entrega:\n(Articulo: " + a.getTitulo() + " — $" + a.getPrecioFormateado() + ")",
            "Confirmar compra", JOptionPane.QUESTION_MESSAGE);

        if (dir == null || dir.trim().isEmpty()) return;

        // Reservar el articulo de forma segura ante compras concurrentes
        try {
            a = articuloDAO.reservarParaCompra(a.getId(), usuarioActual.getId());
        } catch (ArticuloNoDisponibleException | OperacionNoAutorizadaException ex) {
            Validaciones.mostrarError(this, ex.getMessage());
            cargarArticulos();
            return;
        }

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCompradorId      (usuarioActual.getId());
        pedido.setArticuloId       (a.getId());
        pedido.setDireccionEntrega (dir.trim());
        pedido.setTituloArticulo   (a.getTitulo());
        pedido.setPrecioArticulo   (a.getPrecio());

        if (!pedidoDAO.insertar(pedido)) {
            Validaciones.mostrarError(this, "No se pudo crear el pedido.");
            return;
        }

        // Crear transaccion
        Transaccion tx = new Transaccion();
        tx.setPedidoId   (pedido.getId());
        tx.setCompradorId(usuarioActual.getId());
        tx.setVendedorId (a.getVendedorId());
        tx.setMonto      (a.getPrecio());
        tx.setNombreComprador(usuarioActual.getNombreCompleto());
        tx.setNombreVendedor (a.getNombreVendedor());

        if (!txDAO.procesar(tx)) {
            Validaciones.mostrarError(this, "Error al procesar el pago.");
            return;
        }

        // Marcar articulo como vendido
        new ArticuloDAO().cambiarEstado(a.getId(), Articulo.Estado.VENDIDO);

        // Notificar al vendedor
        notifDAO.insertar(new Notificacion(a.getVendedorId(),
            "Venta exitosa: " + a.getTitulo(),
            "Tu articulo fue comprado. Ref: " + tx.getReferencia() + ". Recibiras $" + String.format("%.2f", tx.getMontoVendedor()),
            Notificacion.Tipo.PAGO));

        // Generar comprobante
        String ruta = markettogo.util.GenerarComprobante.generar(tx, pedido);

        String msg = "Compra realizada!\nReferencia: " + tx.getReferencia() +
                     "\nMonto: $" + String.format("%.2f", tx.getMonto()) +
                     "\nComision (5%): $" + String.format("%.2f", tx.getComision());
        if (ruta != null) {
            msg += "\n\nComprobante guardado en: " + ruta;
            if (Validaciones.confirmar(this, msg + "\n\nAbrir comprobante ahora?")) {
                markettogo.util.GenerarComprobante.abrirComprobante(ruta);
            }
        } else {
            Validaciones.mostrarExito(this, msg);
        }

        cargarArticulos();
    }

    private void publicarArticulo() {
        new PublicarArticuloDialog(this, usuarioActual).setVisible(true);
        cargarArticulos();
    }

    private void verMisArticulos() {
        new MisArticulosFrame(usuarioActual).setVisible(true);
    }

    private void verMisPedidos() {
        new MisPedidosFrame(usuarioActual).setVisible(true);
    }

    private void verTrueques() {
        new TruequeFrame(usuarioActual).setVisible(true);
    }

    private void verNotificaciones() {
        List<markettogo.modelo.Notificacion> notifs = notifDAO.listarPorUsuario(usuarioActual.getId());
        if (notifs.isEmpty()) {
            Validaciones.mostrarExito(this, "No tienes notificaciones.");
            return;
        }
        StringBuilder sb = new StringBuilder("<html><b>Tus notificaciones:</b><br><br>");
        for (markettogo.modelo.Notificacion n : notifs) {
            sb.append("<b>[").append(n.getTipo()).append("]</b> ")
              .append(n.getTitulo()).append("<br>")
              .append(n.getMensaje()).append("<br><br>");
            notifDAO.marcarLeida(n.getId());
        }
        sb.append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(), "Notificaciones", JOptionPane.INFORMATION_MESSAGE);
    }

    private void salir() {
        if (Validaciones.confirmar(this, "Deseas cerrar sesion?")) {
            hiloActivo = false;
            hiloNotificaciones.interrupt();
            SesionManager.limpiar();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
