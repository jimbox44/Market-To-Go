package markettogo.modelo;

import java.util.Date;

/**
 * Modelo de Trueque para Market-To-Go.
 */
public class Trueque {

    public enum Estado {
        PROPUESTO, ACEPTADO, RECHAZADO, CANCELADO, COMPLETADO
    }

    private int    id;
    private int    solicitanteId;
    private String nombreSolicitante;
    private int    receptorId;
    private String nombreReceptor;
    private int    articuloOfrecidoId;
    private String tituloArticuloOfrecido;
    private int    articuloSolicitadoId;
    private String tituloArticuloSolicitado;
    private Estado estado;
    private String mensaje;
    private Date   fechaSolicitud;
    private Date   fechaRespuesta;

    public Trueque() {}

    // ── Getters y Setters ──────────────────────────────────
    public int    getId()                          { return id; }
    public void   setId(int id)                    { this.id = id; }

    public int    getSolicitanteId()               { return solicitanteId; }
    public void   setSolicitanteId(int sid)        { this.solicitanteId = sid; }

    public String getNombreSolicitante()            { return nombreSolicitante; }
    public void   setNombreSolicitante(String n)    { this.nombreSolicitante = n; }

    public int    getReceptorId()                  { return receptorId; }
    public void   setReceptorId(int rid)           { this.receptorId = rid; }

    public String getNombreReceptor()               { return nombreReceptor; }
    public void   setNombreReceptor(String n)       { this.nombreReceptor = n; }

    public int    getArticuloOfrecidoId()           { return articuloOfrecidoId; }
    public void   setArticuloOfrecidoId(int id)     { this.articuloOfrecidoId = id; }

    public String getTituloArticuloOfrecido()        { return tituloArticuloOfrecido; }
    public void   setTituloArticuloOfrecido(String t){ this.tituloArticuloOfrecido = t; }

    public int    getArticuloSolicitadoId()         { return articuloSolicitadoId; }
    public void   setArticuloSolicitadoId(int id)   { this.articuloSolicitadoId = id; }

    public String getTituloArticuloSolicitado()        { return tituloArticuloSolicitado; }
    public void   setTituloArticuloSolicitado(String t){ this.tituloArticuloSolicitado = t; }

    public Estado getEstado()                      { return estado; }
    public void   setEstado(Estado e)              { this.estado = e; }

    public String getMensaje()                     { return mensaje; }
    public void   setMensaje(String m)             { this.mensaje = m; }

    public Date   getFechaSolicitud()              { return fechaSolicitud; }
    public void   setFechaSolicitud(Date f)        { this.fechaSolicitud = f; }

    public Date   getFechaRespuesta()              { return fechaRespuesta; }
    public void   setFechaRespuesta(Date f)        { this.fechaRespuesta = f; }

    @Override
    public String toString() {
        return "Trueque #" + id + ": " + tituloArticuloOfrecido + " <-> " + tituloArticuloSolicitado + " [" + estado + "]";
    }
}
