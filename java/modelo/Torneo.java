package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

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
    private String estado = "PENDIENTE";

    @ManyToOne
    @JoinColumn(name = "id_club", nullable = true)
    private Club clubOrganizador;

    @ManyToOne
    @JoinColumn(name = "id_federacion", nullable = true)
    private Federacion federacionOrganizador;

    // SOLUCIÓN: Usamos Set en lugar de List para permitir múltiples fetch EAGER
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Partido> partidos = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "inscripcion", 
        joinColumns = @JoinColumn(name = "id_torneo"),
        inverseJoinColumns = @JoinColumn(name = "id_equipo")
    )
    private Set<Equipo> equipos = new HashSet<>();

    public Torneo() {}

    // Getters
    public int getIdTorneo() { return id_torneo; }
    public String getNombre() { return nombre; }
    public String getLocalizacion() { return localizacion; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public String getCategoria() { return categoria; }
    public String getNormas() { return normas; }
    public String getEstado() { return estado; }
    public Club getClubOrganizador() { return clubOrganizador; }
    public Federacion getFederacionOrganizador() { return federacionOrganizador; }
    public Set<Partido> getPartidos() { return partidos; }
    public Set<Equipo> getEquipos() { return equipos; }

    // Setters
    public void setIdTorneo(int id_torneo) { this.id_torneo = id_torneo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setNormas(String normas) { this.normas = normas; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setClubOrganizador(Club clubOrganizador) { this.clubOrganizador = clubOrganizador; }
    public void setFederacionOrganizador(Federacion federacionOrganizador) { this.federacionOrganizador = federacionOrganizador; }
    public void setPartidos(Set<Partido> partidos) { this.partidos = partidos; }
    public void setEquipos(Set<Equipo> equipos) { this.equipos = equipos; }
}