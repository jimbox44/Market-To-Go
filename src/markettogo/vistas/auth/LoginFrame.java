package markettogo.vistas.auth;

import markettogo.dao.UsuarioDAO;
import markettogo.modelo.Usuario;
import markettogo.util.SesionManager;
import markettogo.util.Validaciones;
import markettogo.vistas.Navegador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de inicio de sesion de Market-To-Go.
 */
public class LoginFrame extends JFrame {

    private JTextField  txtEmail;
    private JPasswordField txtPass;
    private JButton     btnLogin;
    private JButton     btnRegistro;
    private JLabel      lblError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Market-To-Go — Iniciar Sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(420, 480);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Validaciones.GRIS_FONDO);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Validaciones.GRIS_FONDO);

        // ── Encabezado ──
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Validaciones.AZUL_PRIMARIO);
        header.setPreferredSize(new Dimension(420, 120));

        JLabel lblLogo = new JLabel("Market-To-Go");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Marketplace Local con Entrega a Domicilio");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(new Color(180, 210, 240));

        JPanel colHeader = new JPanel();
        colHeader.setLayout(new BoxLayout(colHeader, BoxLayout.Y_AXIS));
        colHeader.setOpaque(false);
        colHeader.add(lblLogo);
        colHeader.add(Box.createVerticalStrut(6));
        colHeader.add(lblSub);
        header.add(colHeader);

        // ── Formulario ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Validaciones.GRIS_FONDO);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Email
        JLabel lblEmail = new JLabel("Correo electronico (admin@markettogo.com)");
        lblEmail.setFont(Validaciones.FUENTE_SUBTITULO);
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lblEmail, gbc);

        txtEmail = new JTextField();
        txtEmail.setFont(Validaciones.FUENTE_NORMAL);
        txtEmail.setPreferredSize(new Dimension(320, 36));
        gbc.gridy = 1;
        form.add(txtEmail, gbc);

        // Contrasena
        JLabel lblPass = new JLabel("Contraseña (admin123)");
        lblPass.setFont(Validaciones.FUENTE_SUBTITULO);
        gbc.gridy = 2;
        form.add(lblPass, gbc);

        txtPass = new JPasswordField();
        txtPass.setFont(Validaciones.FUENTE_NORMAL);
        txtPass.setPreferredSize(new Dimension(320, 36));
        gbc.gridy = 3;
        form.add(txtPass, gbc);

        // Error
        lblError = new JLabel(" ");
        lblError.setFont(Validaciones.FUENTE_PEQUENA);
        lblError.setForeground(Validaciones.ROJO_ERROR);
        gbc.gridy = 4;
        form.add(lblError, gbc);

        // Boton login
        btnLogin = Validaciones.botonPrimario("Iniciar Sesion");
        btnLogin.setPreferredSize(new Dimension(320, 40));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 6, 0);
        form.add(btnLogin, gbc);

        // Separador
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 4, 0);
        form.add(new JSeparator(), gbc);

        // Boton registro
        JPanel panelReg = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelReg.setOpaque(false);
        JLabel lblNoTienes = new JLabel("No tienes cuenta?  ");
        lblNoTienes.setFont(Validaciones.FUENTE_NORMAL);
        btnRegistro = new JButton("Registrarse");
        btnRegistro.setFont(Validaciones.FUENTE_NORMAL);
        btnRegistro.setForeground(Validaciones.AZUL_PRIMARIO);
        btnRegistro.setBorderPainted(false);
        btnRegistro.setContentAreaFilled(false);
        btnRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelReg.add(lblNoTienes);
        panelReg.add(btnRegistro);
        gbc.gridy = 7;
        form.add(panelReg, gbc);

        panelPrincipal.add(header, BorderLayout.NORTH);
        panelPrincipal.add(form,   BorderLayout.CENTER);
        setContentPane(panelPrincipal);

        // ── Acciones ──
        btnLogin.addActionListener(e -> intentarLogin());
        txtPass.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });
        btnRegistro.addActionListener(e -> abrirRegistro());
    }

    private void intentarLogin() {
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPass.getPassword());

        if (Validaciones.campoVacio(email) || Validaciones.campoVacio(pass)) {
            lblError.setText("Por favor complete todos los campos.");
            return;
        }
        if (!Validaciones.emailValido(email)) {
            lblError.setText("Correo electronico invalido.");
            return;
        }

        lblError.setText("Verificando...");
        btnLogin.setEnabled(false);

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override protected Usuario doInBackground() {
                return usuarioDAO.autenticar(email, pass);
            }
            @Override protected void done() {
                Usuario u;
                try {
                    u = get();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    lblError.setText("Error de conexion. Verifique la base de datos.");
                    btnLogin.setEnabled(true);
                    return;
                }

                if (u == null) {
                    lblError.setText("Credenciales incorrectas o cuenta inactiva.");
                    btnLogin.setEnabled(true);
                    return;
                }

                try {
                    abrirPantallaSegunRol(u);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    lblError.setText("Error al abrir la pantalla: " + ex.getClass().getSimpleName());
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void abrirPantallaSegunRol(Usuario u) {
        SesionManager.guardar(u);
        dispose();
        Navegador.abrirPantallaPrincipal(u);
    }

    private void abrirRegistro() {
        new RegistroFrame(this).setVisible(true);
    }
}
