package markettogo.excepciones;

/**
 * Se lanza cuando un articulo ya no puede comprarse:
 * fue eliminado o otro comprador lo adquirio primero.
 */
public class ArticuloNoDisponibleException extends MarketToGoException {
    public ArticuloNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
