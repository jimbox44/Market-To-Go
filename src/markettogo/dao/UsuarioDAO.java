package markettogo.dao;

import markettogo.conexion.Conexion;
import markettogo.modelo.Usuario;
import markettogo.modelo.Usuario.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de Usuario en MySQL.
 */
public class UsuarioDAO {

    private Connection con() {
        return Conexion.getInstance().getConexion();
    }

    // ── Autenticacion ──────────────────────────────────────
    /**
     * Verifica email y contrasena (MD5). Retorna el usuario o null.
     */
    public Usuario autenticar(String email, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE email=? AND contrasena=MD5(?) AND activo=1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error autenticar: " + e.getMessage());
        }
        return null;
    }

    // ── CRUD ───────────────────────────────────────────────
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre,apellido,email,contrasena,telefono,direccion,rol) VALUES (?,?,?,MD5(?),?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getDireccion());
            ps.setString(7, u.getRol().name());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) u.setId(keys.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre=?,apellido=?,telefono=?,direccion=?,rol=?,activo=? WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getDireccion());
            ps.setString(5, u.getRol().name());
            ps.setInt   (6, u.isActivo() ? 1 : 0);
            ps.setInt   (7, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarContrasena(int usuarioId, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET contrasena=MD5(?) WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, nuevaContrasena);
            ps.setInt   (2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiar contrasena: " + e.getMessage());
            return false;
        }
    }

    public boolean desactivar(int id) {
        String sql = "UPDATE usuarios SET activo=0 WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ── Consultas ──────────────────────────────────────────
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarTodos: " + e.getMessage());
        }
        return lista;
    }

    public List<Usuario> listarRepartidores() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol='REPARTIDOR' AND activo=1 ORDER BY nombre";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listarRepartidores: " + e.getMessage());
        }
        return lista;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscarPorId: " + e.getMessage());
        }
        return null;
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error emailExiste: " + e.getMessage());
        }
        return false;
    }

    // ── Mapeo ResultSet -> Objeto ──────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = Usuario.crear(Rol.valueOf(rs.getString("rol")));
        u.setId           (rs.getInt   ("id"));
        u.setNombre       (rs.getString("nombre"));
        u.setApellido     (rs.getString("apellido"));
        u.setEmail        (rs.getString("email"));
        u.setContrasena   (rs.getString("contrasena"));
        u.setTelefono     (rs.getString("telefono"));
        u.setDireccion    (rs.getString("direccion"));
        u.setActivo       (rs.getInt   ("activo") == 1);
        u.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return u;
    }
}
