package markettogo.modelo;

import java.util.List;

/**
 * Usuario con rol COMPRADOR_VENDEDOR: publica, compra e intercambia articulos.
 */
public class CompradorVendedor extends Usuario {

    public CompradorVendedor() {
        setRol(Rol.COMPRADOR_VENDEDOR);
    }

    @Override
    public List<String> getPermisos() {
        return List.of(
            "Publicar articulos",
            "Comprar articulos",
            "Proponer y aceptar trueques",
            "Ver mis pedidos"
        );
    }

    @Override
    public String getDescripcionRol() {
        return "Comprador / Vendedor";
    }
}
