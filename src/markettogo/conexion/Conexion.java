package markettogo.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton para manejar la conexion a la base de datos MySQL.
 * Market-To-Go — Programacion Cliente-Servidor Concurrente
 */
public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/markettogo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "admin123";

    private static Conexion instancia;
    private Connection conexion;

    private Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Retorna la unica instancia de la conexion (Singleton).
     */
    public static synchronized Conexion getInstance() {
        if (instancia == null || instancia.conexion == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    /**
     * Retorna el objeto Connection de JDBC.
     * Si la conexion se cerro, la vuelve a abrir.
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                instancia = new Conexion();
            }
        } catch (SQLException e) {
            instancia = new Conexion();
        }
        return conexion;
    }

    /**
     * Cierra la conexion activa.
     */
    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}
