package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Categorias.
 */
public class CategoriaDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    public boolean insertar(Categoria c) {
        String sql = "INSERT INTO categorias (nombre, descripcion) VALUES (?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) c.setId(keys.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar categoria: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Categoria c) {
        String sql = "UPDATE categorias SET nombre=?,descripcion=?,activa=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt   (3, c.isActiva() ? 1 : 0);
            ps.setInt   (4, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar categoria: " + e.getMessage());
            return false;
        }
    }

    public List<Categoria> listarActivas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE activa=1 ORDER BY nombre";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarActivas: " + e.getMessage());
        }
        return lista;
    }

    public List<Categoria> listarTodas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarTodas categorias: " + e.getMessage());
        }
        return lista;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(
            rs.getInt    ("id"),
            rs.getString ("nombre"),
            rs.getString ("descripcion"),
            rs.getInt    ("activa") == 1
        );
    }
}
