package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.modelo.Transaccion;
import markettogo.modelo.Transaccion.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO para operaciones de Transaccion en MySQL.
 */
public class TransaccionDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    /**
     * Procesa el pago de un pedido aplicando 5% de comision.
     */
    public boolean procesar(Transaccion t) {
        t.calcularComision();
        t.setReferencia("MTG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        t.setEstado(Estado.COMPLETADA);

        String sql = "INSERT INTO transacciones (pedido_id,comprador_id,vendedor_id,monto,comision,monto_vendedor,estado,referencia) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, t.getPedidoId());
            ps.setInt   (2, t.getCompradorId());
            ps.setInt   (3, t.getVendedorId());
            ps.setDouble(4, t.getMonto());
            ps.setDouble(5, t.getComision());
            ps.setDouble(6, t.getMontoVendedor());
            ps.setString(7, t.getEstado().name());
            ps.setString(8, t.getReferencia());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) t.setId(keys.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error procesar transaccion: " + e.getMessage());
            return false;
        }
    }

    public List<Transaccion> listarPorComprador(int compradorId) {
        return listar("WHERE t.comprador_id=" + compradorId);
    }

    public List<Transaccion> listarPorVendedor(int vendedorId) {
        return listar("WHERE t.vendedor_id=" + vendedorId);
    }

    public List<Transaccion> listarTodas() {
        return listar("");
    }

    public Transaccion buscarPorPedido(int pedidoId) {
        List<Transaccion> lista = listar("WHERE t.pedido_id=" + pedidoId);
        return lista.isEmpty() ? null : lista.get(0);
    }

    private List<Transaccion> listar(String filtro) {
        List<Transaccion> lista = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "uc.nombre AS ncomprador, uv.nombre AS nvendedor " +
                     "FROM transacciones t " +
                     "JOIN usuarios uc ON t.comprador_id=uc.id " +
                     "JOIN usuarios uv ON t.vendedor_id=uv.id " +
                     filtro + " ORDER BY t.fecha DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listar transacciones: " + e.getMessage());
        }
        return lista;
    }

    private Transaccion mapear(ResultSet rs) throws SQLException {
        Transaccion t = new Transaccion();
        t.setId            (rs.getInt   ("id"));
        t.setPedidoId      (rs.getInt   ("pedido_id"));
        t.setCompradorId   (rs.getInt   ("comprador_id"));
        t.setNombreComprador(rs.getString("ncomprador"));
        t.setVendedorId    (rs.getInt   ("vendedor_id"));
        t.setNombreVendedor (rs.getString("nvendedor"));
        t.setMonto         (rs.getDouble("monto"));
        t.setComision      (rs.getDouble("comision"));
        t.setMontoVendedor (rs.getDouble("monto_vendedor"));
        t.setEstado        (Estado.valueOf(rs.getString("estado")));
        t.setReferencia    (rs.getString("referencia"));
        t.setFecha         (rs.getTimestamp("fecha"));
        return t;
    }
}
