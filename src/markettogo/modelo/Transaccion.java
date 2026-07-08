package markettogo.modelo;

import java.util.Date;

/**
 * Modelo de Transaccion para Market-To-Go.
 */
public class Transaccion {

    public enum Estado {
        PROCESANDO, COMPLETADA, FALLIDA, REVERTIDA
    }

    private int    id;
    private int    pedidoId;
    private int    compradorId;
    private String nombreComprador;
    private int    vendedorId;
    private String nombreVendedor;
    private double monto;
    private double comision;
    private double montoVendedor;
    private Estado estado;
    private String referencia;
    private Date   fecha;

    public Transaccion() {}

    // ── Getters y Setters ──────────────────────────────────
    public int    getId()                    { return id; }
    public void   setId(int id)              { this.id = id; }

    public int    getPedidoId()              { return pedidoId; }
    public void   setPedidoId(int pid)       { this.pedidoId = pid; }

    public int    getCompradorId()           { return compradorId; }
    public void   setCompradorId(int cid)    { this.compradorId = cid; }

    public String getNombreComprador()        { return nombreComprador; }
    public void   setNombreComprador(String n){ this.nombreComprador = n; }

    public int    getVendedorId()            { return vendedorId; }
    public void   setVendedorId(int vid)     { this.vendedorId = vid; }

    public String getNombreVendedor()         { return nombreVendedor; }
    public void   setNombreVendedor(String n) { this.nombreVendedor = n; }

    public double getMonto()                 { return monto; }
    public void   setMonto(double m)         { this.monto = m; }

    public double getComision()              { return comision; }
    public void   setComision(double c)      { this.comision = c; }

    public double getMontoVendedor()         { return montoVendedor; }
    public void   setMontoVendedor(double m) { this.montoVendedor = m; }

    public Estado getEstado()                { return estado; }
    public void   setEstado(Estado e)        { this.estado = e; }

    public String getReferencia()            { return referencia; }
    public void   setReferencia(String r)    { this.referencia = r; }

    public Date   getFecha()                 { return fecha; }
    public void   setFecha(Date f)           { this.fecha = f; }

    /**
     * Calcula la comision del 5% y el monto neto al vendedor.
     */
    public void calcularComision() {
        this.comision      = this.monto * 0.05;
        this.montoVendedor = this.monto - this.comision;
    }

    @Override
    public String toString() {
        return "Transaccion #" + referencia + " - $" + String.format("%.2f", monto) + " [" + estado + "]";
    }
}
