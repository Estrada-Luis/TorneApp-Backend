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

    // --- NUEVOS CAMPOS PARA GESTIÓN DE TORNEOS ---
    
    @Column(name = "goles")
    private int goles;

    @Column(name = "puntos")
    private int puntos; // 3 por victoria, 1 empate, 0 derrota

    @Column(name = "grupo")
    private String grupo; // "A", "B", "C", etc.

    public Juega() {}

    public Juega(Equipo equipo, Partido partido) {
        this.equipo = equipo;
        this.partido = partido;
        // Inicializamos la clave compuesta
        this.id = new JuegaId(
                equipo.getIdEquipo(), 
                partido.getIdPartido() 
        );
        // Valores por defecto
        this.goles = 0;
        this.puntos = 0;
    }

    // --- GETTERS Y SETTERS ---

    public JuegaId getId() { return id; }
    public void setId(JuegaId id) { this.id = id; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
}
