package markettogo.excepciones;

/**
 * Excepcion base de las reglas de negocio de Market-To-Go.
 * Permite atrapar cualquier error especifico del dominio con un solo catch.
 */
public abstract class MarketToGoException extends Exception {
    public MarketToGoException(String mensaje) {
        super(mensaje);
    }
}
