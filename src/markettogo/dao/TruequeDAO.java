package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.modelo.Trueque;
import markettogo.modelo.Trueque.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones de Trueque en MySQL.
 */
public class TruequeDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    public boolean insertar(Trueque t) {
        String sql = "INSERT INTO trueques (solicitante_id,receptor_id,articulo_ofrecido_id,articulo_solicitado_id,mensaje) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, t.getSolicitanteId());
            ps.setInt   (2, t.getReceptorId());
            ps.setInt   (3, t.getArticuloOfrecidoId());
            ps.setInt   (4, t.getArticuloSolicitadoId());
            ps.setString(5, t.getMensaje());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) t.setId(keys.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar trueque: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int truequeId, Estado estado) {
        String sql = "UPDATE trueques SET estado=?, fecha_respuesta=NOW() WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt   (2, truequeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizarEstado trueque: " + e.getMessage());
            return false;
        }
    }

    public List<Trueque> listarPorUsuario(int usuarioId) {
        return listar("WHERE (tr.solicitante_id=" + usuarioId + " OR tr.receptor_id=" + usuarioId + ")");
    }

    public List<Trueque> listarSolicitudesRecibidas(int receptorId) {
        return listar("WHERE tr.receptor_id=" + receptorId + " AND tr.estado='PROPUESTO'");
    }

    public List<Trueque> listarTodos() {
        return listar("");
    }

    private List<Trueque> listar(String filtro) {
        List<Trueque> lista = new ArrayList<>();
        String sql = "SELECT tr.*, " +
                     "us.nombre AS nsolic, ur.nombre AS nrecep, " +
                     "ao.titulo AS tofrecido, as2.titulo AS tsolic " +
                     "FROM trueques tr " +
                     "JOIN usuarios us ON tr.solicitante_id=us.id " +
                     "JOIN usuarios ur ON tr.receptor_id=ur.id " +
                     "JOIN articulos ao  ON tr.articulo_ofrecido_id=ao.id " +
                     "JOIN articulos as2 ON tr.articulo_solicitado_id=as2.id " +
                     filtro + " ORDER BY tr.fecha_solicitud DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listar trueques: " + e.getMessage());
        }
        return lista;
    }

    private Trueque mapear(ResultSet rs) throws SQLException {
        Trueque t = new Trueque();
        t.setId                      (rs.getInt   ("id"));
        t.setSolicitanteId           (rs.getInt   ("solicitante_id"));
        t.setNombreSolicitante       (rs.getString("nsolic"));
        t.setReceptorId              (rs.getInt   ("receptor_id"));
        t.setNombreReceptor          (rs.getString("nrecep"));
        t.setArticuloOfrecidoId      (rs.getInt   ("articulo_ofrecido_id"));
        t.setTituloArticuloOfrecido  (rs.getString("tofrecido"));
        t.setArticuloSolicitadoId    (rs.getInt   ("articulo_solicitado_id"));
        t.setTituloArticuloSolicitado(rs.getString("tsolic"));
        t.setEstado                  (Estado.valueOf(rs.getString("estado")));
        t.setMensaje                 (rs.getString("mensaje"));
        t.setFechaSolicitud          (rs.getTimestamp("fecha_solicitud"));
        t.setFechaRespuesta          (rs.getTimestamp("fecha_respuesta"));
        return t;
    }
}
