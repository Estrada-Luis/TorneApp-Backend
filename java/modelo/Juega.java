package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "juega")
public class Juega implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private JuegaId id;

    @ManyToOne
    @MapsId("idEquipo")
    @JoinColumn(name = "id_equipo")
    private Equipo equipo;

    @ManyToOne
    @MapsId("idPartido")
    @JoinColumn(name = "id_partido")
    private Partido partido;

    public Juega() {}

    public Juega(Equipo equipo, Partido partido) {
        this.equipo = equipo;
        this.partido = partido;
        // CORRECCIÓN AQUÍ: Asegúrate de que los nombres coincidan con tus getters de Equipo y Partido
        this.id = new JuegaId(
                equipo.getIdEquipo(), 
                partido.getIdPartido() 
        );
    }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public JuegaId getId() { return id; }
    public void setId(JuegaId id) { this.id = id; }
}
