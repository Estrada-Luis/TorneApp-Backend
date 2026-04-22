package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "entrenador")
public class Entrenador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEntrenador")
    private int idEntrenador;

    @Column(nullable = false)
    private String telefono;

    @OneToOne(cascade = CascadeType.ALL) // <--- ESTO SOLUCIONA EL ERROR
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    public Entrenador() {}

    public Entrenador(String telefono, Equipo equipo, Usuario usuario) {
        this.telefono = telefono;
        this.equipo = equipo;
        this.usuario = usuario;
    }

    // Getters y Setters
    public int getIdEntrenador() { 
    	return idEntrenador; 
    	}
    
    public void setIdEntrenador(int idEntrenador) {
    	this.idEntrenador = idEntrenador; 
    	}
    
    public String getTelefono() { 
    	return telefono; 
    	}
    
    public void setTelefono(String telefono) { 
    	this.telefono = telefono; 
    	}
    
    public Usuario getUsuario() { 
    	return usuario; 
    	}
    
    public void setUsuario(Usuario usuario) {
    	this.usuario = usuario;
    	}
    
    public Equipo getEquipo() {
    	return equipo; 
    	}
    
    public void setEquipo(Equipo equipo) {
    	this.equipo = equipo;
    	}
}