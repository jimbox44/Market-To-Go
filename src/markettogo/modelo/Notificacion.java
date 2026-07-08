package markettogo.modelo;

import java.util.Date;

/**
 * Modelo de Notificacion para Market-To-Go.
 */
public class Notificacion {

    public enum Tipo {
        PEDIDO, TRUEQUE, PAGO, SISTEMA
    }

    private int    id;
    private int    usuarioId;
    private String titulo;
    private String mensaje;
    private boolean leida;
    private Tipo   tipo;
    private Date   fecha;

    public Notificacion() {}

    public Notificacion(int usuarioId, String titulo, String mensaje, Tipo tipo) {
        this.usuarioId = usuarioId;
        this.titulo    = titulo;
        this.mensaje   = mensaje;
        this.tipo      = tipo;
        this.leida     = false;
    }

    public int     getId()                  { return id; }
    public void    setId(int id)            { this.id = id; }

    public int     getUsuarioId()           { return usuarioId; }
    public void    setUsuarioId(int uid)    { this.usuarioId = uid; }

    public String  getTitulo()              { return titulo; }
    public void    setTitulo(String t)      { this.titulo = t; }

    public String  getMensaje()             { return mensaje; }
    public void    setMensaje(String m)     { this.mensaje = m; }

    public boolean isLeida()                { return leida; }
    public void    setLeida(boolean l)      { this.leida = l; }

    public Tipo    getTipo()                { return tipo; }
    public void    setTipo(Tipo t)          { this.tipo = t; }

    public Date    getFecha()               { return fecha; }
    public void    setFecha(Date f)         { this.fecha = f; }

    @Override
    public String toString() { return "[" + tipo + "] " + titulo; }
}
