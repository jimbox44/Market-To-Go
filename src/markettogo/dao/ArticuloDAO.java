package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.excepciones.ArticuloNoDisponibleException;
import markettogo.excepciones.OperacionNoAutorizadaException;
import markettogo.modelo.Articulo;
import markettogo.modelo.Articulo.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de Articulo en MySQL.
 */
public class ArticuloDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    public boolean insertar(Articulo a) {
        String sql = "INSERT INTO articulos (vendedor_id,categoria_id,titulo,descripcion,precio,ubicacion,disponible_trueque) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, a.getVendedorId());
            ps.setInt   (2, a.getCategoriaId());
            ps.setString(3, a.getTitulo());
            ps.setString(4, a.getDescripcion());
            ps.setDouble(5, a.getPrecio());
            ps.setString(6, a.getUbicacion());
            ps.setInt   (7, a.isDisponibleTrueque() ? 1 : 0);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) a.setId(keys.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar articulo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Articulo a) {
        String sql = "UPDATE articulos SET categoria_id=?,titulo=?,descripcion=?,precio=?,ubicacion=?,disponible_trueque=?,estado=? WHERE id=? AND vendedor_id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt   (1, a.getCategoriaId());
            ps.setString(2, a.getTitulo());
            ps.setString(3, a.getDescripcion());
            ps.setDouble(4, a.getPrecio());
            ps.setString(5, a.getUbicacion());
            ps.setInt   (6, a.isDisponibleTrueque() ? 1 : 0);
            ps.setString(7, a.getEstado().name());
            ps.setInt   (8, a.getId());
            ps.setInt   (9, a.getVendedorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar articulo: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int articuloId, Estado estado) {
        String sql = "UPDATE articulos SET estado=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt   (2, articuloId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiarEstado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reserva un articulo para compra de forma atomica: el UPDATE solo aplica
     * si el articulo sigue DISPONIBLE, evitando que dos compradores concurrentes
     * se queden con el mismo articulo (condicion de carrera).
     */
    public Articulo reservarParaCompra(int articuloId, int compradorId)
            throws ArticuloNoDisponibleException, OperacionNoAutorizadaException {
        Articulo a = buscarPorId(articuloId);
        if (a == null) {
            throw new ArticuloNoDisponibleException("El articulo ya no existe.");
        }
        if (a.getVendedorId() == compradorId) {
            throw new OperacionNoAutorizadaException("No puedes comprar tu propio articulo.");
        }

        String sql = "UPDATE articulos SET estado='RESERVADO' WHERE id=? AND estado='DISPONIBLE'";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, articuloId);
            if (ps.executeUpdate() == 0) {
                throw new ArticuloNoDisponibleException(
                    "El articulo ya no esta disponible: otro comprador lo adquirio primero.");
            }
        } catch (SQLException e) {
            throw new ArticuloNoDisponibleException("Error de base de datos al reservar el articulo: " + e.getMessage());
        }

        a.setEstado(Estado.RESERVADO);
        return a;
    }

    // ── Consultas ──────────────────────────────────────────
    public List<Articulo> listarDisponibles() {
        return listarConFiltro("WHERE a.estado='DISPONIBLE'");
    }

    public List<Articulo> listarTodos() {
        return listarConFiltro("");
    }

    public List<Articulo> buscarPorTitulo(String texto) {
        List<Articulo> lista = new ArrayList<>();
        String sql = "SELECT a.*, u.nombre AS nvendedor, c.nombre AS ncategoria " +
                     "FROM articulos a " +
                     "JOIN usuarios u ON a.vendedor_id=u.id " +
                     "JOIN categorias c ON a.categoria_id=c.id " +
                     "WHERE a.estado='DISPONIBLE' AND a.titulo LIKE ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error buscarPorTitulo: " + e.getMessage());
        }
        return lista;
    }

    public List<Articulo> listarPorCategoria(int categoriaId) {
        return listarConFiltro("WHERE a.estado='DISPONIBLE' AND a.categoria_id=" + categoriaId);
    }

    public List<Articulo> listarPorVendedor(int vendedorId) {
        return listarConFiltro("WHERE a.vendedor_id=" + vendedorId);
    }

    public List<Articulo> listarDisponiblesTrueque() {
        return listarConFiltro("WHERE a.estado='DISPONIBLE' AND a.disponible_trueque=1");
    }

    public Articulo buscarPorId(int id) {
        List<Articulo> lista = listarConFiltro("WHERE a.id=" + id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    private List<Articulo> listarConFiltro(String filtro) {
        List<Articulo> lista = new ArrayList<>();
        String sql = "SELECT a.*, u.nombre AS nvendedor, c.nombre AS ncategoria " +
                     "FROM articulos a " +
                     "JOIN usuarios u ON a.vendedor_id=u.id " +
                     "JOIN categorias c ON a.categoria_id=c.id " +
                     filtro + " ORDER BY a.fecha_publicacion DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarConFiltro: " + e.getMessage());
        }
        return lista;
    }

    private Articulo mapear(ResultSet rs) throws SQLException {
        Articulo a = new Articulo();
        a.setId               (rs.getInt   ("id"));
        a.setVendedorId       (rs.getInt   ("vendedor_id"));
        a.setNombreVendedor   (rs.getString("nvendedor"));
        a.setCategoriaId      (rs.getInt   ("categoria_id"));
        a.setNombreCategoria  (rs.getString("ncategoria"));
        a.setTitulo           (rs.getString("titulo"));
        a.setDescripcion      (rs.getString("descripcion"));
        a.setPrecio           (rs.getDouble("precio"));
        a.setUbicacion        (rs.getString("ubicacion"));
        a.setEstado           (Estado.valueOf(rs.getString("estado")));
        a.setDisponibleTrueque(rs.getInt("disponible_trueque") == 1);
        a.setFechaPublicacion (rs.getTimestamp("fecha_publicacion"));
        return a;
    }
}
