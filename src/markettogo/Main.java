package markettogo;

import markettogo.dao.UsuarioDAO;
import markettogo.modelo.Usuario;
import markettogo.util.SesionManager;
import markettogo.vistas.Navegador;
import markettogo.vistas.auth.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Usuario admin por defecto:
 *   Email:      admin@markettogo.com
 *   Contrasena: admin123
 */
public class Main {

    public static void main(String[] args) {

        // Aplicar look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se usa el look and feel por defecto de Java si falla
        }

        // Lanzar la interfaz en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            Usuario sesionGuardada = SesionManager.cargar();
            if (sesionGuardada != null) {
                Usuario actual = new UsuarioDAO().buscarPorId(sesionGuardada.getId());
                if (actual != null && actual.isActivo()) {
                    Navegador.abrirPantallaPrincipal(actual);
                    return;
                }
                SesionManager.limpiar();
            }
            new LoginFrame().setVisible(true);
        });
    }
}
