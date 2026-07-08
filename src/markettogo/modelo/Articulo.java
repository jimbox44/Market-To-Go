package markettogo.modelo;

import java.util.Date;

/**
 * Modelo de Articulo para Market-To-Go.
 */
public class Articulo {

    public enum Estado {
        DISPONIBLE, RESERVADO, VENDIDO, INACTIVO
    }

    private int     id;
    private int     vendedorId;
    private String  nombreVendedor;   // campo auxiliar para JOIN
    private int     categoriaId;
    private String  nombreCategoria;  // campo auxiliar para JOIN
    private String  titulo;
    private String  descripcion;
    private double  precio;
    private String  ubicacion;
    private Estado  estado;
    private boolean disponibleTrueque;
    private Date    fechaPublicacion;

    public Articulo() {}

    public Articulo(int id, int vendedorId, int categoriaId, String titulo,
                    String descripcion, double precio, String ubicacion,
                    Estado estado, boolean disponibleTrueque, Date fechaPublicacion) {
        this.id                 = id;
        this.vendedorId         = vendedorId;
        this.categoriaId        = categoriaId;
        this.titulo             = titulo;
        this.descripcion        = descripcion;
        this.precio             = precio;
        this.ubicacion          = ubicacion;
        this.estado             = estado;
        this.disponibleTrueque  = disponibleTrueque;
        this.fechaPublicacion   = fechaPublicacion;
    }

    // ── Getters y Setters ──────────────────────────────────
    public int     getId()                   { return id; }
    public void    setId(int id)             { this.id = id; }

    public int     getVendedorId()               { return vendedorId; }
    public void    setVendedorId(int vid)         { this.vendedorId = vid; }

    public String  getNombreVendedor()           { return nombreVendedor; }
    public void    setNombreVendedor(String nv)  { this.nombreVendedor = nv; }

    public int     getCategoriaId()              { return categoriaId; }
    public void    setCategoriaId(int cid)       { this.categoriaId = cid; }

    public String  getNombreCategoria()          { return nombreCategoria; }
    public void    setNombreCategoria(String nc) { this.nombreCategoria = nc; }

    public String  getTitulo()               { return titulo; }
    public void    setTitulo(String t)       { this.titulo = t; }

    public String  getDescripcion()          { return descripcion; }
    public void    setDescripcion(String d)  { this.descripcion = d; }

    public double  getPrecio()               { return precio; }
    public void    setPrecio(double p)       { this.precio = p; }

    public String  getUbicacion()            { return ubicacion; }
    public void    setUbicacion(String u)    { this.ubicacion = u; }

    public Estado  getEstado()               { return estado; }
    public void    setEstado(Estado e)       { this.estado = e; }

    public boolean isDisponibleTrueque()          { return disponibleTrueque; }
    public void    setDisponibleTrueque(boolean b) { this.disponibleTrueque = b; }

    public Date    getFechaPublicacion()           { return fechaPublicacion; }
    public void    setFechaPublicacion(Date fecha) { this.fechaPublicacion = fecha; }

    public String  getPrecioFormateado() {
        return String.format("%.2f", precio);
    }

    @Override
    public String toString() { return titulo + " - $" + getPrecioFormateado(); }
}
