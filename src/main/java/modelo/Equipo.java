package modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
// Importante: Estos dos imports gestionan la serialización JSON
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "equipo")
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

 // En tu clase Equipo del Backend:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("idEquipo") // 🚩 Añade esto
    private int id_equipo;
    
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, length = 4)
    private String codigoVinculacion;

    @Column(length = 50)
    private String categoria;

    @Column(length = 50)
    private String division;

    // --- 🛡️ SOLUCIÓN AL BUCLE INFINITO ---
    // Al pedir un Club, el servidor envía sus Equipos. 
    // Al llegar aquí, ignoramos la lista de "equipos" dentro del club 
    // para que Jackson no vuelva a empezar el ciclo.
    @ManyToOne
    @JoinColumn(name = "id_club", nullable = false)
    @JsonIgnoreProperties("equipos") 
    private Club club;

    @OneToMany(mappedBy = "equipo", fetch = FetchType.EAGER) 
    private List<Usuario> jugadores;

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

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    public List<Usuario> getJugadores() { return jugadores; }
    public void setJugadores(List<Usuario> jugadores) { this.jugadores = jugadores; }

    @Override
    public String toString() {
        return nombre + " (" + (categoria != null ? categoria : "Sin categoría") + ")";
    }
}
