package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "club")
public class Club implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_club;

    @Column(unique = true, nullable = false)
    private String cif;

    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    
    // --- IMPORTANTE: FALTA LA CONTRASEÑA ---
    private String password; 

    // --- Atributos de validación ---
    private boolean validado = false;
    private String motivoRechazo;

    @ManyToOne
    @JoinColumn(name = "id_federacion", nullable = false)
    private Federacion federacion;

    @OneToMany(mappedBy = "club", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Equipo> equipos = new HashSet<>();

    public Club() {}

    // --- GETTERS ---
    public int getIdClub() { return this.id_club; }
    public String getNombre() { return this.nombre; }
    public String getCif() { return this.cif; }
    public String getDireccion() { return this.direccion; }
    public String getTelefono() { return this.telefono; }
    public String getCorreo() { return this.correo; }
    
    // El método que te fallaba en el Servicio:
    public String getPassword() { return this.password; } 
    
    public boolean isValidado() { return this.validado; }
    public String getMotivoRechazo() { return this.motivoRechazo; }
    public Federacion getFederacion() { return this.federacion; }
    public Set<Equipo> getEquipos() { return this.equipos; }

    // --- SETTERS ---
    public void setIdClub(int id_club) { this.id_club = id_club; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCif(String cif) { this.cif = cif; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    // Setter de password:
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