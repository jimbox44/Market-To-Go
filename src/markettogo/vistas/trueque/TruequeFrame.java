package markettogo.vistas.trueque;

import markettogo.dao.*;
import markettogo.modelo.*;
import markettogo.util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pantalla de gestion de trueques.
 */
public class TruequeFrame extends JFrame {

    private final Usuario usuario;
    private JTable tblTrueques;
    private DefaultTableModel modelo;
    private JTabbedPane tabs;
    private JButton btnProponer, btnAceptar, btnRechazar, btnRefrescar;

    private final TruequeDAO  truequeDAO  = new TruequeDAO();
    private final ArticuloDAO articuloDAO = new ArticuloDAO();
    private final NotificacionDAO notifDAO = new NotificacionDAO();

    public TruequeFrame(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        cargar();
    }

    private void initComponents() {
        setTitle("Trueques — " + usuario.getNombre());
        setSize(860, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        add(Validaciones.panelEncabezado("Mis trueques", "Propone o acepta intercambios de articulos"), BorderLayout.NORTH);

        String[] cols = {"ID", "Yo ofrezco", "Solicito", "Contraparte", "Estado", "Fecha"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTrueques = new JTable(modelo);
        tblTrueques.setRowHeight(26);
        tblTrueques.setFont(Validaciones.FUENTE_NORMAL);
        Validaciones.estilizarHeader(tblTrueques);
        tblTrueques.getColumnModel().getColumn(0).setMinWidth(0);
        tblTrueques.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tblTrueques);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.setBackground(Validaciones.GRIS_FONDO);
        btnRefrescar = Validaciones.botonSecundario("Refrescar");
        btnProponer  = Validaciones.botonPrimario("Proponer trueque");
        btnAceptar   = Validaciones.botonPrimario("Aceptar");
        btnAceptar.setBackground(Validaciones.VERDE_EXITO);
        btnRechazar  = Validaciones.botonPeligro("Rechazar");
        botones.add(btnRefrescar);
        botones.add(btnRechazar);
        botones.add(btnAceptar);
        botones.add(btnProponer);
        add(botones, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargar());
        btnProponer .addActionListener(e -> proponerTrueque());
        btnAceptar  .addActionListener(e -> responder(Trueque.Estado.ACEPTADO));
        btnRechazar .addActionListener(e -> responder(Trueque.Estado.RECHAZADO));
    }

    private void cargar() {
        modelo.setRowCount(0);
        truequeDAO.listarPorUsuario(usuario.getId()).forEach(t -> {
            boolean soyElSolicitante = t.getSolicitanteId() == usuario.getId();
            modelo.addRow(new Object[]{
                t.getId(),
                soyElSolicitante ? t.getTituloArticuloOfrecido()   : t.getTituloArticuloSolicitado(),
                soyElSolicitante ? t.getTituloArticuloSolicitado() : t.getTituloArticuloOfrecido(),
                soyElSolicitante ? t.getNombreReceptor()           : t.getNombreSolicitante(),
                t.getEstado(),
                t.getFechaSolicitud()
            });
        });
    }

    private void proponerTrueque() {
        // Seleccionar articulo propio
        List<Articulo> propios = articuloDAO.listarPorVendedor(usuario.getId());
        if (propios.isEmpty()) {
            Validaciones.mostrarError(this, "No tienes articulos publicados para ofrecer.");
            return;
        }
        Articulo[] arrPropios = propios.toArray(new Articulo[0]);
        Articulo miArticulo = (Articulo) JOptionPane.showInputDialog(this,
            "Selecciona el articulo que ofreces:", "Tu articulo",
            JOptionPane.QUESTION_MESSAGE, null, arrPropios, arrPropios[0]);
        if (miArticulo == null) return;

        // Seleccionar articulo ajeno disponible para trueque
        List<Articulo> disponibles = articuloDAO.listarDisponiblesTrueque();
        disponibles.removeIf(a -> a.getVendedorId() == usuario.getId());
        if (disponibles.isEmpty()) {
            Validaciones.mostrarError(this, "No hay articulos disponibles para trueque en este momento.");
            return;
        }
        Articulo[] arrDisp = disponibles.toArray(new Articulo[0]);
        Articulo articuloDeseado = (Articulo) JOptionPane.showInputDialog(this,
            "Selecciona el articulo que deseas:", "Articulo deseado",
            JOptionPane.QUESTION_MESSAGE, null, arrDisp, arrDisp[0]);
        if (articuloDeseado == null) return;

        String mensaje = JOptionPane.showInputDialog(this, "Mensaje opcional para la contraparte:");

        Trueque t = new Trueque();
        t.setSolicitanteId        (usuario.getId());
        t.setReceptorId           (articuloDeseado.getVendedorId());
        t.setArticuloOfrecidoId   (miArticulo.getId());
        t.setArticuloSolicitadoId (articuloDeseado.getId());
        t.setMensaje              (mensaje);

        if (truequeDAO.insertar(t)) {
            notifDAO.insertar(new Notificacion(
                articuloDeseado.getVendedorId(),
                "Propuesta de trueque recibida",
                usuario.getNombreCompleto() + " quiere intercambiar \"" + miArticulo.getTitulo() +
                "\" por tu articulo \"" + articuloDeseado.getTitulo() + "\".",
                Notificacion.Tipo.TRUEQUE));
            Validaciones.mostrarExito(this, "Propuesta de trueque enviada.");
            cargar();
        } else {
            Validaciones.mostrarError(this, "Error al enviar la propuesta.");
        }
    }

    private void responder(Trueque.Estado nuevoEstado) {
        int fila = tblTrueques.getSelectedRow();
        if (fila < 0) { Validaciones.mostrarError(this, "Selecciona un trueque."); return; }
        int id = (int) modelo.getValueAt(fila, 0);

        // Verificar que el usuario sea el receptor
        List<Trueque> recibidos = truequeDAO.listarSolicitudesRecibidas(usuario.getId());
        boolean esReceptor = recibidos.stream().anyMatch(t -> t.getId() == id);
        if (!esReceptor) {
            Validaciones.mostrarError(this, "Solo puedes aceptar o rechazar trueques recibidos que esten en estado PROPUESTO.");
            return;
        }

        String accion = nuevoEstado == Trueque.Estado.ACEPTADO ? "aceptar" : "rechazar";
        if (!Validaciones.confirmar(this, "Deseas " + accion + " este trueque?")) return;

        if (truequeDAO.actualizarEstado(id, nuevoEstado)) {
            Validaciones.mostrarExito(this, "Trueque " + nuevoEstado.name().toLowerCase() + " exitosamente.");
            cargar();
        } else {
            Validaciones.mostrarError(this, "Error al actualizar el trueque.");
        }
    }
}
