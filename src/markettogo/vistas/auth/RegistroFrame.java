package markettogo.vistas.auth;

import markettogo.dao.UsuarioDAO;
import markettogo.modelo.Usuario;
import markettogo.util.Validaciones;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogo de registro de nuevos usuarios.
 */
public class RegistroFrame extends JDialog {

    private JTextField  txtNombre, txtApellido, txtEmail, txtTelefono, txtDireccion;
    private JPasswordField txtPass, txtConfirm;
    private JButton     btnRegistrar, btnCancelar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public RegistroFrame(JFrame parent) {
        super(parent, "Registro de Usuario", true);
        initComponents();
    }

    private void initComponents() {
        setSize(460, 560);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Validaciones.GRIS_FONDO);

        // Encabezado
        panel.add(Validaciones.panelEncabezado("Crear cuenta", "Completa tus datos para registrarte"), BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Validaciones.GRIS_FONDO);
        form.setBorder(BorderFactory.createEmptyBorder(16, 30, 16, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 4, 0);

        String[] etiquetas = {"Nombre *", "Apellido *", "Correo electronico *",
                              "Contrasena *", "Confirmar contrasena *",
                              "Telefono", "Direccion"};
        JComponent[] campos = {
            txtNombre    = new JTextField(),
            txtApellido  = new JTextField(),
            txtEmail     = new JTextField(),
            txtPass      = new JPasswordField(),
            txtConfirm   = new JPasswordField(),
            txtTelefono  = new JTextField(),
            txtDireccion = new JTextField()
        };

        for (int i = 0; i < etiquetas.length; i++) {
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(Validaciones.FUENTE_SUBTITULO);
            gbc.gridy = i * 2; form.add(lbl, gbc);

            campos[i].setFont(Validaciones.FUENTE_NORMAL);
            campos[i].setPreferredSize(new Dimension(380, 32));
            gbc.gridy = i * 2 + 1; form.add(campos[i], gbc);
        }

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.setOpaque(false);
        btnRegistrar = Validaciones.botonPrimario("Registrarse");
        btnCancelar  = Validaciones.botonSecundario("Cancelar");
        botones.add(btnCancelar);
        botones.add(btnRegistrar);

        gbc.gridy = etiquetas.length * 2;
        gbc.insets = new Insets(12, 0, 0, 0);
        form.add(botones, gbc);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        setContentPane(panel);

        // Acciones
        btnCancelar .addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> registrar());
    }

    private void registrar() {
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String email     = txtEmail.getText().trim();
        String pass      = new String(txtPass.getPassword());
        String confirm   = new String(txtConfirm.getPassword());
        String telefono  = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (Validaciones.campoVacio(nombre) || Validaciones.campoVacio(apellido) ||
            Validaciones.campoVacio(email)  || Validaciones.campoVacio(pass)) {
            Validaciones.mostrarError(this, "Por favor complete los campos obligatorios (*).");
            return;
        }
        if (!Validaciones.emailValido(email)) {
            Validaciones.mostrarError(this, "El correo electronico no es valido.");
            return;
        }
        if (!Validaciones.contrasenaSuficiente(pass)) {
            Validaciones.mostrarError(this, "La contrasena debe tener al menos 6 caracteres.");
            return;
        }
        if (!pass.equals(confirm)) {
            Validaciones.mostrarError(this, "Las contrasenas no coinciden.");
            return;
        }
        if (usuarioDAO.emailExiste(email)) {
            Validaciones.mostrarError(this, "Ya existe una cuenta con ese correo electronico.");
            return;
        }

        Usuario u = Usuario.crear(Usuario.Rol.COMPRADOR_VENDEDOR);
        u.setNombre    (nombre);
        u.setApellido  (apellido);
        u.setEmail     (email);
        u.setContrasena(pass);
        u.setTelefono  (telefono);
        u.setDireccion (direccion);

        if (usuarioDAO.insertar(u)) {
            Validaciones.mostrarExito(this, "Cuenta creada exitosamente. Ahora puedes iniciar sesion.");
            dispose();
        } else {
            Validaciones.mostrarError(this, "Error al crear la cuenta. Intente de nuevo.");
        }
    }
}
