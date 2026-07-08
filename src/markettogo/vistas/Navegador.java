package markettogo.vistas;

import markettogo.modelo.Usuario;
import markettogo.vistas.admin.AdminFrame;
import markettogo.vistas.repartidor.RepartidorFrame;
import markettogo.vistas.usuario.CatalogoFrame;

import javax.swing.*;

/**
 * Punto unico para abrir la ventana principal segun el rol del usuario.
 * Quien llama no necesita conocer las subclases de Usuario: el rol
 * ya viene resuelto de forma polimorfica en el objeto recibido.
 */
public class Navegador {

    public static void abrirPantallaPrincipal(Usuario u) {
        JFrame ventana = switch (u.getRol()) {
            case ADMINISTRADOR      -> new AdminFrame(u);
            case REPARTIDOR         -> new RepartidorFrame(u);
            case COMPRADOR_VENDEDOR -> new CatalogoFrame(u);
        };
        ventana.setVisible(true);
    }
}
