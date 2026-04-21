package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "participa")
public class Participa implements Serializable {

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

    public Participa() {}

    public Participa(Equipo equipo, Torneo torneo) {
        this.equipo = equipo;
        this.torneo = torneo;
        this.id = new ParticipaId(equipo.getIdEquipo(), torneo.getIdTorneo());
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
    public ParticipaId getId() {
		return id;
	}
    	public void setId(ParticipaId id) {
		this.id = id;
	}
    
}