package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "equipo")
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_equipo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, length = 4)
    private String codigoVinculacion;

    // Campo necesario para la visualización en la consulta de clubs
    @Column(length = 50)
    private String categoria;

    @ManyToOne
    @JoinColumn(name = "id_club", nullable = false)
    private Club club;

    public Equipo() {}

    public Equipo(String nombre, Club club, String categoria) {
        this.nombre = nombre;
        this.club = club;
        this.categoria = categoria;
    }

    // --- GETTERS Y SETTERS ---
    public int getIdEquipo() { return id_equipo; }
    public void setIdEquipo(int id_equipo) { this.id_equipo = id_equipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoVinculacion() { return codigoVinculacion; }
    public void setCodigoVinculacion(String codigoVinculacion) { this.codigoVinculacion = codigoVinculacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    @Override
    public String toString() {
        return nombre + " (" + (categoria != null ? categoria : "Sin categoría") + ")";
    }
}