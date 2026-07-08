package markettogo.modelo;

/**
 * Modelo de Categoria para Market-To-Go.
 */
public class Categoria {

    private int     id;
    private String  nombre;
    private String  descripcion;
    private boolean activa;

    public Categoria() {}

    public Categoria(int id, String nombre, String descripcion, boolean activa) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.activa      = activa;
    }

    public int     getId()                  { return id; }
    public void    setId(int id)            { this.id = id; }

    public String  getNombre()              { return nombre; }
    public void    setNombre(String n)      { this.nombre = n; }

    public String  getDescripcion()         { return descripcion; }
    public void    setDescripcion(String d) { this.descripcion = d; }

    public boolean isActiva()               { return activa; }
    public void    setActiva(boolean a)     { this.activa = a; }

    @Override
    public String toString() { return nombre; }
}
