package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "federacion")
public class Federacion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_federacion;

    @Column(nullable = false)
    private String nombre;
    private String direccion;
    private String correo;
    private String telefono;

    public Federacion() {}

    public Federacion(String nombre, String direccion, String correo, String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.correo = correo;
        this.telefono = telefono;
    }

    // Getters y Setters
    public int getIdFederacion() { 
    	return id_federacion; 
    	}
    
    public String getNombre() { 
    	return nombre; 
    	}
    
    public void setNombre(String nombre) {
    	this.nombre = nombre; 
    	}
    
    public String getDireccion() {
    	return direccion;
    	}
    
    public String getCorreo() {
    	return correo; 
    	}
    
    public String getTelefono() {
    	return telefono; 
    	}

	public void setIdFederacion(int i) {
		this.id_federacion = i;
		
	}
}

    public String getTelefono() {
    	return telefono; 
    	}
}
