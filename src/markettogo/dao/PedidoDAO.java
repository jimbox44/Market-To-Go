package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.modelo.Pedido;
import markettogo.modelo.Pedido.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones de Pedido en MySQL.
 */
public class PedidoDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    public boolean insertar(Pedido p) {
        String sql = "INSERT INTO pedidos (comprador_id,articulo_id,direccion_entrega,notas) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, p.getCompradorId());
            ps.setInt   (2, p.getArticuloId());
            ps.setString(3, p.getDireccionEntrega());
            ps.setString(4, p.getNotas());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int pedidoId, Estado estado) {
        String sql = "UPDATE pedidos SET estado_pedido=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt   (2, pedidoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizarEstado pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarRepartidor(int pedidoId, int repartidorId) {
        String sql = "UPDATE pedidos SET repartidor_id=?, estado_pedido='CONFIRMADO' WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, repartidorId);
            ps.setInt(2, pedidoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error asignarRepartidor: " + e.getMessage());
            return false;
        }
    }

    public List<Pedido> listarPorComprador(int compradorId) {
        return listar("WHERE p.comprador_id=" + compradorId);
    }

    public List<Pedido> listarPorRepartidor(int repartidorId) {
        return listar("WHERE p.repartidor_id=" + repartidorId + " AND p.estado_pedido IN ('CONFIRMADO','EN_CAMINO')");
    }

    public List<Pedido> listarTodos() {
        return listar("");
    }

    public List<Pedido> listarPendientes() {
        return listar("WHERE p.estado_pedido='PENDIENTE'");
    }

    private List<Pedido> listar(String filtro) {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "uc.nombre AS ncomprador, " +
                     "a.titulo  AS tarticulo, " +
                     "a.precio  AS particulo, " +
                     "ur.nombre AS nrepartidor " +
                     "FROM pedidos p " +
                     "JOIN usuarios  uc ON p.comprador_id=uc.id " +
                     "JOIN articulos a  ON p.articulo_id=a.id " +
                     "LEFT JOIN usuarios ur ON p.repartidor_id=ur.id " +
                     filtro + " ORDER BY p.fecha_pedido DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId               (rs.getInt   ("id"));
        p.setCompradorId      (rs.getInt   ("comprador_id"));
        p.setNombreComprador  (rs.getString("ncomprador"));
        p.setArticuloId       (rs.getInt   ("articulo_id"));
        p.setTituloArticulo   (rs.getString("tarticulo"));
        p.setPrecioArticulo   (rs.getDouble("particulo"));
        p.setRepartidorId     (rs.getInt   ("repartidor_id"));
        p.setNombreRepartidor (rs.getString("nrepartidor"));
        p.setEstadoPedido     (Estado.valueOf(rs.getString("estado_pedido")));
        p.setDireccionEntrega (rs.getString("direccion_entrega"));
        p.setNotas            (rs.getString("notas"));
        p.setFechaPedido      (rs.getTimestamp("fecha_pedido"));
        p.setFechaEntrega     (rs.getTimestamp("fecha_entrega"));
        return p;
    }
}
