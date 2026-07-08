package markettogo.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Modelo base de Usuario para Market-To-Go.
 * Es abstracta: cada rol se representa con una subclase concreta
 * ({@link Administrador}, {@link CompradorVendedor}, {@link Repartidor})
 * que define su propio conjunto de permisos (polimorfismo).
 * Implementa Serializable para poder persistir la sesion activa en disco.
 */
public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Rol {
        ADMINISTRADOR, COMPRADOR_VENDEDOR, REPARTIDOR
    }

    private int    id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String telefono;
    private String direccion;
    private Rol    rol;
    private boolean activo;
    private Date   fechaRegistro;

    protected Usuario() {}

    /**
     * Fabrica que retorna la subclase concreta correspondiente al rol,
     * de modo que el resto del sistema solo trabaja con el tipo Usuario.
     */
    public static Usuario crear(Rol rol) {
        return switch (rol) {
            case ADMINISTRADOR       -> new Administrador();
            case REPARTIDOR          -> new Repartidor();
            case COMPRADOR_VENDEDOR  -> new CompradorVendedor();
        };
    }

    /** Lista de acciones permitidas para este rol (polimorfico). */
    public abstract List<String> getPermisos();

    /** Nombre legible del rol (polimorfico). */
    public abstract String getDescripcionRol();

    // ── Getters y Setters ──────────────────────────────────
    public int     getId()           { return id; }
    public void    setId(int id)     { this.id = id; }

    public String  getNombre()              { return nombre; }
    public void    setNombre(String n)      { this.nombre = n; }

    public String  getApellido()            { return apellido; }
    public void    setApellido(String a)    { this.apellido = a; }

    public String  getEmail()               { return email; }
    public void    setEmail(String e)       { this.email = e; }

    public String  getContrasena()          { return contrasena; }
    public void    setContrasena(String c)  { this.contrasena = c; }

    public String  getTelefono()            { return telefono; }
    public void    setTelefono(String t)    { this.telefono = t; }

    public String  getDireccion()           { return direccion; }
    public void    setDireccion(String d)   { this.direccion = d; }

    public Rol     getRol()                 { return rol; }
    public void    setRol(Rol r)            { this.rol = r; }

    public boolean isActivo()               { return activo; }
    public void    setActivo(boolean a)     { this.activo = a; }

    public Date    getFechaRegistro()            { return fechaRegistro; }
    public void    setFechaRegistro(Date fecha)  { this.fechaRegistro = fecha; }

    public String  getNombreCompleto() { return nombre + " " + apellido; }

    @Override
    public String toString() { return getNombreCompleto() + " <" + email + ">"; }
}
