package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.modelo.Notificacion;
import markettogo.modelo.Notificacion.Tipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Notificaciones.
 */
public class NotificacionDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    public boolean insertar(Notificacion n) {
        String sql = "INSERT INTO notificaciones (usuario_id,titulo,mensaje,tipo) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt   (1, n.getUsuarioId());
            ps.setString(2, n.getTitulo());
            ps.setString(3, n.getMensaje());
            ps.setString(4, n.getTipo().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar notificacion: " + e.getMessage());
            return false;
        }
    }

    public boolean marcarLeida(int notifId) {
        String sql = "UPDATE notificaciones SET leida=1 WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, notifId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marcarLeida: " + e.getMessage());
            return false;
        }
    }

    public List<Notificacion> listarPorUsuario(int usuarioId) {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE usuario_id=? ORDER BY fecha DESC LIMIT 50";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listar notificaciones: " + e.getMessage());
        }
        return lista;
    }

    public int contarNoLeidas(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM notificaciones WHERE usuario_id=? AND leida=0";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error contarNoLeidas: " + e.getMessage());
        }
        return 0;
    }

    private Notificacion mapear(ResultSet rs) throws SQLException {
        Notificacion n = new Notificacion();
        n.setId       (rs.getInt    ("id"));
        n.setUsuarioId(rs.getInt    ("usuario_id"));
        n.setTitulo   (rs.getString ("titulo"));
        n.setMensaje  (rs.getString ("mensaje"));
        n.setLeida    (rs.getInt    ("leida") == 1);
        n.setTipo     (Tipo.valueOf (rs.getString("tipo")));
        n.setFecha    (rs.getTimestamp("fecha"));
        return n;
    }
}
