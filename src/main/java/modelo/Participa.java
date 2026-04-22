package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "participa")
public class Participa implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ParticipaId id;

    @ManyToOne
    @MapsId("idEquipo")
    @JoinColumn(name = "id_equipo")
    private Equipo equipo;

    @ManyToOne
    @MapsId("idTorneo")
    @JoinColumn(name = "id_torneo")
    private Torneo torneo;

    // Nuevo campo para gestionar la fase de grupos
    @Column(name = "grupo")
    private String grupo; // Ej: "A", "B", "C"...

    public Participa() {}

    public Participa(Equipo equipo, Torneo torneo) {
        this.equipo = equipo;
        this.torneo = torneo;
        // Inicializamos la clave compuesta con los IDs de las entidades
        this.id = new ParticipaId(equipo.getIdEquipo(), torneo.getIdTorneo());
    }

    // --- GETTERS Y SETTERS ---

    public ParticipaId getId() {
        return id;
    }

    public void setId(ParticipaId id) {
        this.id = id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}