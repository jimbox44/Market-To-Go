package markettogo.util;

import markettogo.modelo.Pedido;
import markettogo.modelo.Transaccion;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Genera comprobantes de transaccion como archivos de texto.
 * Market-To-Go
 */
public class GenerarComprobante {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
     * Genera un archivo .txt con el comprobante de la transaccion
     * y lo guarda en la carpeta "comprobantes" del proyecto.
     * @return ruta del archivo generado, o null si fallo.
     */
    public static String generar(Transaccion t, Pedido p) {
        String carpeta = "comprobantes";
        new File(carpeta).mkdirs();

        String nombreArchivo = carpeta + File.separator + "comprobante_" + t.getReferencia() + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println("============================================================");
            pw.println("                   MARKET-TO-GO");
            pw.println("          Comprobante de Transaccion");
            pw.println("============================================================");
            pw.println();
            pw.println("  Referencia  : " + t.getReferencia());
            pw.println("  Fecha       : " + SDF.format(t.getFecha() != null ? t.getFecha() : new Date()));
            pw.println("  Estado      : " + t.getEstado());
            pw.println();
            pw.println("------------------------------------------------------------");
            pw.println("  DETALLE DEL ARTICULO");
            pw.println("------------------------------------------------------------");
            pw.println("  Articulo    : " + p.getTituloArticulo());
            pw.println("  Vendedor    : " + t.getNombreVendedor());
            pw.println("  Comprador   : " + t.getNombreComprador());
            pw.println("  Entrega en  : " + p.getDireccionEntrega());
            pw.println();
            pw.println("------------------------------------------------------------");
            pw.println("  RESUMEN DE PAGO");
            pw.println("------------------------------------------------------------");
            pw.printf ("  Monto total : $ %10.2f%n", t.getMonto());
            pw.printf ("  Comision 5%% : $ %10.2f%n", t.getComision());
            pw.printf ("  Al vendedor : $ %10.2f%n", t.getMontoVendedor());
            pw.println();
            pw.println("============================================================");
            pw.println("  Gracias por usar Market-To-Go");
            pw.println("  www.markettogo.com");
            pw.println("============================================================");

            return nombreArchivo;

        } catch (IOException e) {
            System.err.println("Error generando comprobante: " + e.getMessage());
            return null;
        }
    }

    /**
     * Abre el archivo de comprobante con el visor de texto del sistema.
     */
    public static void abrirComprobante(String ruta) {
        try {
            java.awt.Desktop.getDesktop().open(new File(ruta));
        } catch (Exception e) {
            System.err.println("No se pudo abrir el comprobante: " + e.getMessage());
        }
    }
}
