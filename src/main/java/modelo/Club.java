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
    
    // AQUÍ AÑADIMOS EL PASSWORD
    private String password;
    
    private boolean validado = false;

    @ManyToOne
    @JoinColumn(name = "id_federacion", nullable = false)
    private Federacion federacion;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Equipo> equipos = new HashSet<>();

    public Club() {}

    // Actualizamos el constructor para incluir el password
    public Club(String nombre, String cif, String direccion, String telefono, String correo, String password, boolean validado, Federacion federacion) {
        this.nombre = nombre;
        this.cif = cif;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.password = password;
        this.validado = validado;
        this.federacion = federacion;
    }

    // --- GETTERS ---
    public int getIdClub() { return id_club; }
    public String getNombre() { return nombre; }
    public String getCif() { return cif; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getPassword() { return password; } // Nuevo Getter
    public boolean isValidado() { return validado; }
    public Federacion getFederacion() { return federacion; }
    public Set<Equipo> getEquipos() { return equipos; }

    // --- SETTERS ---
    public void setIdClub(int id_club) { this.id_club = id_club; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCif(String cif) { this.cif = cif; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setPassword(String password) { this.password = password; } // Nuevo Setter
    public void setValidado(boolean validado) { this.validado = validado; }
    public void setFederacion(Federacion federacion) { this.federacion = federacion; }
    public void setEquipos(Set<Equipo> equipos) { this.equipos = equipos; }

    @Override
    public String toString() {
        return nombre;
    }
}
