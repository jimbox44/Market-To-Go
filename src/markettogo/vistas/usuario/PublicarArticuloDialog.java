package markettogo.vistas.usuario;

import markettogo.dao.ArticuloDAO;
import markettogo.dao.CategoriaDAO;
import markettogo.modelo.Articulo;
import markettogo.modelo.Categoria;
import markettogo.modelo.Usuario;
import markettogo.util.Validaciones;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialogo para publicar un nuevo articulo.
 */
public class PublicarArticuloDialog extends JDialog {

    private final Usuario usuario;
    private JTextField     txtTitulo, txtPrecio, txtUbicacion;
    private JTextArea      txtDescripcion;
    private JComboBox<Categoria> cbCategoria;
    private JCheckBox      chkTrueque;
    private JButton        btnPublicar, btnCancelar;

    private final ArticuloDAO  articuloDAO  = new ArticuloDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public PublicarArticuloDialog(JFrame parent, Usuario usuario) {
        super(parent, "Publicar articulo", true);
        this.usuario = usuario;
        initComponents();
    }

    private void initComponents() {
        setSize(460, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);
        panel.add(Validaciones.panelEncabezado("Publicar articulo", "Completa la informacion de tu articulo"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Validaciones.GRIS_FONDO);
        form.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 2, 0);

        // Titulo
        agregar(form, gbc, 0, "Titulo *", txtTitulo = new JTextField());

        // Categoria
        JLabel lblCat = new JLabel("Categoria *");
        lblCat.setFont(Validaciones.FUENTE_SUBTITULO);
        gbc.gridy = 2; form.add(lblCat, gbc);
        cbCategoria = new JComboBox<>();
        cbCategoria.setFont(Validaciones.FUENTE_NORMAL);
        List<Categoria> cats = categoriaDAO.listarActivas();
        cats.forEach(cbCategoria::addItem);
        gbc.gridy = 3; form.add(cbCategoria, gbc);

        // Precio
        agregar(form, gbc, 4, "Precio (USD) *", txtPrecio = new JTextField());

        // Ubicacion
        agregar(form, gbc, 6, "Ubicacion *", txtUbicacion = new JTextField());

        // Descripcion
        JLabel lblDesc = new JLabel("Descripcion");
        lblDesc.setFont(Validaciones.FUENTE_SUBTITULO);
        gbc.gridy = 8; form.add(lblDesc, gbc);
        txtDescripcion = new JTextArea(3, 30);
        txtDescripcion.setFont(Validaciones.FUENTE_NORMAL);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridy = 9; form.add(scrollDesc, gbc);

        // Trueque
        chkTrueque = new JCheckBox("Disponible para trueque");
        chkTrueque.setFont(Validaciones.FUENTE_NORMAL);
        chkTrueque.setOpaque(false);
        gbc.gridy = 10; gbc.insets = new Insets(8, 0, 4, 0); form.add(chkTrueque, gbc);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        btnCancelar = Validaciones.botonSecundario("Cancelar");
        btnPublicar = Validaciones.botonPrimario("Publicar");
        botones.add(btnCancelar);
        botones.add(btnPublicar);
        gbc.gridy = 11; gbc.insets = new Insets(10, 0, 0, 0);
        form.add(botones, gbc);

        panel.add(form, BorderLayout.CENTER);
        setContentPane(panel);

        btnCancelar.addActionListener(e -> dispose());
        btnPublicar.addActionListener(e -> publicar());
    }

    private void agregar(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Validaciones.FUENTE_SUBTITULO);
        gbc.gridy = row; form.add(lbl, gbc);
        field.setFont(Validaciones.FUENTE_NORMAL);
        field.setPreferredSize(new Dimension(380, 30));
        gbc.gridy = row + 1; form.add(field, gbc);
    }

    private void publicar() {
        String titulo    = txtTitulo.getText().trim();
        String precio    = txtPrecio.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();

        if (Validaciones.campoVacio(titulo) || Validaciones.campoVacio(precio) || Validaciones.campoVacio(ubicacion)) {
            Validaciones.mostrarError(this, "Completa los campos obligatorios (*).");
            return;
        }
        if (!Validaciones.precioValido(precio)) {
            Validaciones.mostrarError(this, "El precio debe ser un numero mayor a 0.");
            return;
        }

        Categoria cat = (Categoria) cbCategoria.getSelectedItem();
        Articulo a = new Articulo();
        a.setVendedorId      (usuario.getId());
        a.setCategoriaId     (cat != null ? cat.getId() : 1);
        a.setTitulo          (titulo);
        a.setDescripcion     (txtDescripcion.getText().trim());
        a.setPrecio          (Double.parseDouble(precio.replace(",", ".")));
        a.setUbicacion       (ubicacion);
        a.setEstado          (Articulo.Estado.DISPONIBLE);
        a.setDisponibleTrueque(chkTrueque.isSelected());

        if (articuloDAO.insertar(a)) {
            Validaciones.mostrarExito(this, "Articulo publicado exitosamente.");
            dispose();
        } else {
            Validaciones.mostrarError(this, "Error al publicar el articulo.");
        }
    }
}
