package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import necesario

@Entity
@Table(name = "club")
public class Club implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_club;

    @Column(unique = true, nullable = false, updatable = false)
    private String cif;

    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String password; 

    private boolean validado = false;
    private String motivoRechazo;

    // --- 🛡️ SEGURIDAD 1: Evitamos bucle con Federación ---
    @ManyToOne
    @JoinColumn(name = "id_federacion", nullable = false)
    @JsonIgnoreProperties("clubes") 
    private Federacion federacion;

    // --- 🛡️ SEGURIDAD 2: Evitamos bucle con Equipos ---
    @OneToMany(mappedBy = "club", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("club") 
    private Set<Equipo> equipos = new HashSet<>();

    public Club() {}

    // MANTENEMOS TUS MÉTODOS EXACTAMENTE IGUAL
    public int getIdClub() { return this.id_club; }
    public String getNombre() { return this.nombre; }
    public String getCif() { return this.cif; }
    public String getDireccion() { return this.direccion; }
    public String getTelefono() { return this.telefono; }
    public String getCorreo() { return this.correo; }
    public String getPassword() { return this.password; } 
    public boolean isValidado() { return this.validado; }
    public String getMotivoRechazo() { return this.motivoRechazo; }
    public Federacion getFederacion() { return this.federacion; }
    public Set<Equipo> getEquipos() { return this.equipos; }

    public void setIdClub(int id_club) { this.id_club = id_club; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCif(String cif) { this.cif = cif; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setPassword(String password) { this.password = password; }
    public void setValidado(boolean validado) { this.validado = validado; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public void setFederacion(Federacion federacion) { this.federacion = federacion; }
    public void setEquipos(Set<Equipo> equipos) { this.equipos = equipos; }

    @Override
    public String toString() {
        return this.nombre;
    }
}