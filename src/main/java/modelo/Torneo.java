package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "torneo")
public class Torneo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_torneo;

    private String nombre;
    private String localizacion;
    private String fechaInicio;
    private String fechaFin;
    private String categoria;
    private String normas;
    
    // Estado inicial para nuevos torneos
    private String estado = "PENDIENTE";
    
    private String pdfUrl; 

    // --- RELACIONES ---

    // El Club puede ser null si organiza la Federación
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_club", nullable = true)
    @JsonIgnoreProperties({"equipos", "federacion", "torneosOrganizados"})
    private Club clubOrganizador;

    // La Federación puede ser null si es un torneo puramente de club (aunque suelen estar vinculados)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_federacion", nullable = true)
    @JsonIgnoreProperties({"clubes", "usuarios", "torneos"})
    private Federacion federacionOrganizador;

    // Partidos del torneo
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("torneo")
    private Set<Partido> partidos = new HashSet<>();

    /**
     * 🚩 RELACIÓN CLAVE: Equipos Inscritos
     * Usamos FetchType.EAGER para que el escritorio no los vea vacíos.
     * La tabla se llama 'inscripcion' para coincidir con el ParticipaDAO.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "inscripcion", 
        joinColumns = @JoinColumn(name = "id_torneo"),
        inverseJoinColumns = @JoinColumn(name = "id_equipo")
    )
    @JsonIgnoreProperties({"club", "jugadores"})
    private Set<Equipo> equipos = new HashSet<>();

    public Torneo() {}

    // --- GETTERS ---
    public int getIdTorneo() { return id_torneo; }
    public String getNombre() { return nombre; }
    public String getLocalizacion() { return localizacion; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public String getCategoria() { return categoria; }
    public String getNormas() { return normas; }
    public String getEstado() { return estado; }
    public String getPdfUrl() { return pdfUrl; }
    public Club getClubOrganizador() { return clubOrganizador; }
    public Federacion getFederacionOrganizador() { return federacionOrganizador; }
    public Set<Partido> getPartidos() { return partidos; }
    public Set<Equipo> getEquipos() { return equipos; }

    // --- SETTERS ---
    public void setIdTorneo(int id_torneo) { this.id_torneo = id_torneo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setNormas(String normas) { this.normas = normas; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public void setClubOrganizador(Club clubOrganizador) { this.clubOrganizador = clubOrganizador; }
    public void setFederacionOrganizador(Federacion federacionOrganizador) { this.federacionOrganizador = federacionOrganizador; }
    public void setPartidos(Set<Partido> partidos) { this.partidos = partidos; }
    public void setEquipos(Set<Equipo> equipos) { this.equipos = equipos; }
}