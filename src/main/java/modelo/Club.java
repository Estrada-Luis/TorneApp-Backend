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
    
    // --- IMPORTANTE: Atributos de validación ---
    private boolean validado = false;
    private String motivoRechazo; // <--- ESTO ES LO QUE FALTABA

    @ManyToOne
    @JoinColumn(name = "id_federacion", nullable = false)
    private Federacion federacion;

    @OneToMany(mappedBy = "club", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Equipo> equipos = new HashSet<>();

    public Club() {}

    public Club(String nombre, String cif, String direccion, String telefono, String correo, boolean validado, Federacion federacion) {
        this.nombre = nombre;
        this.cif = cif;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.validado = validado;
        this.federacion = federacion;
    }

    // --- GETTERS ---
    public int getIdClub() { return this.id_club; }
    public String getNombre() { return this.nombre; }
    public String getCif() { return this.cif; }
    public String getDireccion() { return this.direccion; }
    public String getTelefono() { return this.telefono; }
    public String getCorreo() { return this.correo; }
    public boolean isValidado() { return this.validado; }
    public Federacion getFederacion() { return this.federacion; }
    public Set<Equipo> getEquipos() { return this.equipos; }
    
    // Getter nuevo
    public String getMotivoRechazo() { return this.motivoRechazo; }

    // --- SETTERS ---
    public void setIdClub(int id_club) { this.id_club = id_club; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCif(String cif) { this.cif = cif; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setValidado(boolean validado) { this.validado = validado; }
    public void setFederacion(Federacion federacion) { this.federacion = federacion; }
    public void setEquipos(Set<Equipo> equipos) { this.equipos = equipos; }
    
    // Setter nuevo (El que arregla el error del controlador)
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    @Override
    public String toString() {
        return this.nombre;
    }
}
