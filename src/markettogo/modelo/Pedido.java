package markettogo.modelo;

import java.util.Date;

/**
 * Modelo de Pedido para Market-To-Go.
 */
public class Pedido {

    public enum Estado {
        PENDIENTE, CONFIRMADO, EN_CAMINO, ENTREGADO, CANCELADO
    }

    private int    id;
    private int    compradorId;
    private String nombreComprador;
    private int    articuloId;
    private String tituloArticulo;
    private double precioArticulo;
    private int    repartidorId;
    private String nombreRepartidor;
    private Estado estadoPedido;
    private String direccionEntrega;
    private String notas;
    private Date   fechaPedido;
    private Date   fechaEntrega;

    public Pedido() {}

    // ── Getters y Setters ──────────────────────────────────
    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }

    public int    getCompradorId()             { return compradorId; }
    public void   setCompradorId(int cid)      { this.compradorId = cid; }

    public String getNombreComprador()          { return nombreComprador; }
    public void   setNombreComprador(String n)  { this.nombreComprador = n; }

    public int    getArticuloId()              { return articuloId; }
    public void   setArticuloId(int aid)       { this.articuloId = aid; }

    public String getTituloArticulo()          { return tituloArticulo; }
    public void   setTituloArticulo(String t)  { this.tituloArticulo = t; }

    public double getPrecioArticulo()          { return precioArticulo; }
    public void   setPrecioArticulo(double p)  { this.precioArticulo = p; }

    public int    getRepartidorId()            { return repartidorId; }
    public void   setRepartidorId(int rid)     { this.repartidorId = rid; }

    public String getNombreRepartidor()         { return nombreRepartidor; }
    public void   setNombreRepartidor(String n) { this.nombreRepartidor = n; }

    public Estado getEstadoPedido()            { return estadoPedido; }
    public void   setEstadoPedido(Estado e)    { this.estadoPedido = e; }

    public String getDireccionEntrega()         { return direccionEntrega; }
    public void   setDireccionEntrega(String d) { this.direccionEntrega = d; }

    public String getNotas()                   { return notas; }
    public void   setNotas(String n)           { this.notas = n; }

    public Date   getFechaPedido()             { return fechaPedido; }
    public void   setFechaPedido(Date f)       { this.fechaPedido = f; }

    public Date   getFechaEntrega()            { return fechaEntrega; }
    public void   setFechaEntrega(Date f)      { this.fechaEntrega = f; }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + tituloArticulo + " [" + estadoPedido + "]";
    }
}
