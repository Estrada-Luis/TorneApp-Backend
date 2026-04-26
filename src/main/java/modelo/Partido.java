package modelo;

import java.util.*; // Importante para List, ArrayList, Set, HashSet
import javax.persistence.*;

@Entity
@Table(name = "partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partido")
    private int idPartido;

    private String fecha;
    private String hora;
    private String resultado;
    private String tipoFase; 
    private String nombreFase;
    private boolean actaCerrada = false;

    @ManyToOne
    @JoinColumn(name = "id_torneo", nullable = false)
    private Torneo torneo;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Juega> juegos = new HashSet<>();

    public Partido() {}

    /**
     * ✅ NOMBRE LOCAL: Corregido sin Streams para evitar errores
     */
    public String getNombreLocal() {
        if (juegos == null || juegos.isEmpty()) return "TBD";
        
        List<Juega> lista = new ArrayList<>(juegos);
        // Ordenamos por ID de equipo: el ID más bajo es el Local
        Collections.sort(lista, new Comparator<Juega>() {
            @Override
            public int compare(Juega j1, Juega j2) {
                return Integer.compare(j1.getEquipo().getIdEquipo(), j2.getEquipo().getIdEquipo());
            }
        });
        return lista.get(0).getEquipo().getNombre();
    }

    /**
     * ✅ NOMBRE VISITANTE: Corregido sin Streams
     */
    public String getNombreVisitante() {
        if (juegos == null || juegos.size() < 2) return "TBD";
        
        List<Juega> lista = new ArrayList<>(juegos);
        Collections.sort(lista, new Comparator<Juega>() {
            @Override
            public int compare(Juega j1, Juega j2) {
                return Integer.compare(j1.getEquipo().getIdEquipo(), j2.getEquipo().getIdEquipo());
            }
        });
        return lista.get(1).getEquipo().getNombre();
    }

    /**
     * ✅ RESULTADO: Calculado de los goles reales
     */
    public String getResultado() {
        if (juegos == null || juegos.size() < 2) return (resultado != null) ? resultado : "0-0";
        
        List<Juega> lista = new ArrayList<>(juegos);
        Collections.sort(lista, new Comparator<Juega>() {
            @Override
            public int compare(Juega j1, Juega j2) {
                return Integer.compare(j1.getEquipo().getIdEquipo(), j2.getEquipo().getIdEquipo());
            }
        });
        
        return lista.get(0).getGoles() + "-" + lista.get(1).getGoles();
    }

    // --- GETTERS Y SETTERS ---
    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public Set<Juega> getJuegos() { return juegos; }
    public void setJuegos(Set<Juega> juegos) { this.juegos = juegos; }
    public String getTipoFase() { return tipoFase; }
    public void setTipoFase(String tipoFase) { this.tipoFase = tipoFase; }
    public String getNombreFase() { return nombreFase; }
    public void setNombreFase(String nombreFase) { this.nombreFase = nombreFase; }
    public boolean isActaCerrada() { return actaCerrada; }
    public void setActaCerrada(boolean actaCerrada) { this.actaCerrada = actaCerrada; }
}