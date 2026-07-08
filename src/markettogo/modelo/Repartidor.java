package markettogo.modelo;

import java.util.List;

/**
 * Usuario con rol REPARTIDOR: gestiona la entrega de pedidos asignados.
 */
public class Repartidor extends Usuario {

    public Repartidor() {
        setRol(Rol.REPARTIDOR);
    }

    @Override
    public List<String> getPermisos() {
        return List.of(
            "Ver pedidos asignados",
            "Marcar pedidos como EN CAMINO",
            "Marcar pedidos como ENTREGADO"
        );
    }

    @Override
    public String getDescripcionRol() {
        return "Repartidor";
    }
}
