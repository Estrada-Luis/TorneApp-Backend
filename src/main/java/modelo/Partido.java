package modelo;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partido")
    private int idPartido;

    private String fecha;
    private String hora;
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    private Torneo torneo;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Juega> juegos = new HashSet<>();

    public Partido() {}

    // Métodos inteligentes para el Controller
    public String getNombreLocal() {
        if (juegos != null && !juegos.isEmpty()) {
            return juegos.iterator().next().getEquipo().getNombre();
        }
        return "TBD";
    }

    public String getNombreVisitante() {
        if (juegos != null && juegos.size() >= 2) {
            // Convertimos a array para acceder al segundo elemento si es necesario
            return ((Juega) juegos.toArray()[1]).getEquipo().getNombre();
        }
        return "TBD";
    }

    // Getters y Setters
    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public Set<Juega> getJuegos() { return juegos; }
    public void setJuegos(Set<Juega> juegos) { this.juegos = juegos; }
}