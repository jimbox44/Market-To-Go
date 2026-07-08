package markettogo.excepciones;

/**
 * Se lanza cuando un usuario intenta una operacion que su rol
 * o su relacion con el recurso no le permite realizar.
 */
public class OperacionNoAutorizadaException extends MarketToGoException {
    public OperacionNoAutorizadaException(String mensaje) {
        super(mensaje);
    }
}
