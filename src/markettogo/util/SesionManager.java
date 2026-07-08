package markettogo.util;

import markettogo.modelo.Usuario;

import java.io.*;

/**
 * Persiste la sesion activa serializando el objeto Usuario en disco,
 * para poder restaurarla si la aplicacion se cierra y se vuelve a abrir.
 * Al deserializar se recupera automaticamente la subclase concreta
 * (Administrador, CompradorVendedor o Repartidor) gracias al polimorfismo.
 */
public class SesionManager {

    private static final String ARCHIVO = "sesion.ser";

    public static void guardar(Usuario usuario) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            out.writeObject(usuario);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la sesion: " + e.getMessage());
        }
    }

    public static Usuario cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Usuario) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo restaurar la sesion: " + e.getMessage());
            archivo.delete();
            return null;
        }
    }

    public static void limpiar() {
        new File(ARCHIVO).delete();
    }
}
