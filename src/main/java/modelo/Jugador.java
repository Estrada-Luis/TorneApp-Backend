package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "jugador")
public class Jugador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jugador")
    private int idJugador;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int edad;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    public Jugador() {}

    public Jugador(String nombre, int edad, Equipo equipo) {
        this.nombre = nombre;
        this.edad = edad;
        this.equipo = equipo;
    }

    // --- GETTERS Y SETTERS (Necesarios para que la UI lea los datos) ---
    public int getIdJugador() { 
    	return idJugador; 
    	}
    public void setIdJugador(int idJugador) {
    	this.idJugador = idJugador; 
    	}

    public String getNombre() {
    	return nombre; 
    	}
    
    public void setNombre(String nombre) { 
    	this.nombre = nombre;
    	}

    public int getEdad() {
    	return edad;
    	}
   
    public void setEdad(int edad) { 
    	this.edad = edad; 
    	}

    public Equipo getEquipo() { 
    	return equipo;
    	}
    
    public void setEquipo(Equipo equipo) {
    	this.equipo = equipo;
    	}

    @Override
    public String toString() {
        return String.format("Jugador [nombre=%s, edad=%d, equipo=%s]", 
                             nombre, edad, (equipo != null ? equipo.getNombre() : "Sin equipo"));
    }
}
