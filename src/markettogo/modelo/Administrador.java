package markettogo.modelo;

import java.util.List;

/**
 * Usuario con rol ADMINISTRADOR: gestiona el sistema completo.
 */
public class Administrador extends Usuario {

    public Administrador() {
        setRol(Rol.ADMINISTRADOR);
    }

    @Override
    public List<String> getPermisos() {
        return List.of(
            "Gestionar usuarios",
            "Gestionar categorias",
            "Ver todas las transacciones",
            "Asignar repartidores a pedidos"
        );
    }

    @Override
    public String getDescripcionRol() {
        return "Administrador del sistema";
    }
}
