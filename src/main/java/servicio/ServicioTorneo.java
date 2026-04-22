package servicio;

import dao.TorneoDAO;
import dao.ParticipaDAO;
import dao.JuegaDAO;
import dao.EquipoDAO;
import modelo.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@RestController
@RequestMapping("/api")
public class ServicioTorneo {

    private TorneoDAO torneoDAO;
    private ParticipaDAO participaDAO;
    private JuegaDAO juegaDAO;
    private EquipoDAO equipoDAO;
    private final String UPLOAD_DIR = "uploads/pdfs/";

    public ServicioTorneo() {
        this.torneoDAO = new TorneoDAO();
        this.participaDAO = new ParticipaDAO();
        this.juegaDAO = new JuegaDAO();
        this.equipoDAO = new EquipoDAO();
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
        }
    }

    /**
     * ✅ LISTAR EQUIPOS DE UN TORNEO
     * Ruta: api/equipos/torneo/{id}
     */
    @GetMapping("/equipos/torneo/{id}")
    public List<Equipo> listarEquiposPorTorneo(@PathVariable int id) {
        return participaDAO.getEquiposPorTorneo(id);
    }

    /**
     * ✅ INSCRIPCIÓN DE EQUIPO
     * Ruta: api/torneos/{idTorneo}/inscribir/{idEquipo}
     */
    @PostMapping("/torneos/{idTorneo}/inscribir/{idEquipo}")
    public ResponseEntity<Void> inscribirEquipo(@PathVariable int idTorneo, @PathVariable int idEquipo) {
        try {
            Torneo t = torneoDAO.read(idTorneo);
            Equipo e = equipoDAO.read(idEquipo);
            if (t == null || e == null) return ResponseEntity.notFound().build();

            Participa p = new Participa();
            p.setTorneo(t);
            p.setEquipo(e);
            p.setGrupo("S/G");
            p.setId(new ParticipaId(e.getIdEquipo(), t.getIdTorneo()));

            participaDAO.create(p);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ✅ HISTORIAL: TORNEOS DONDE EL CLUB PARTICIPA
     * Ruta: api/torneos/inscritos/club/{id}
     */
    @GetMapping("/torneos/inscritos/club/{id}")
    public List<Torneo> listarTorneosInscritos(@PathVariable int id) {
        return torneoDAO.findInscritosByClub(id);
    }

    /**
     * ✅ HISTORIAL: TORNEOS CREADOS POR EL CLUB (El que faltaba)
     * Ruta: api/torneos/organizados/club/{id}
     */
    @GetMapping("/torneos/organizados/club/{id}")
    public List<Torneo> listarTorneosOrganizados(@PathVariable int id) {
        return torneoDAO.findOrganizadosByClub(id);
    }

    /**
     * ✅ GENERAL: LISTAR TODOS LOS TORNEOS
     * Ruta: api/torneos/listar
     */
    @GetMapping("/torneos/listar")
    public List<Torneo> listarParaMovil() {
        return torneoDAO.readAll();
    }

    @GetMapping("/torneos/{id}")
    public ResponseEntity<Torneo> obtenerTorneoParaMovil(@PathVariable int id) {
        Torneo t = torneoDAO.read(id);
        return (t != null) ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    /**
     * ✅ CLASIFICACIÓN DEL TORNEO
     * Ruta: api/torneos/{id}/clasificacion
     */
    @GetMapping("/torneos/{id}/clasificacion")
    public List<FilaClasificacion> obtenerClasificacion(@PathVariable int id) {
        List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(id);
        List<Juega> todosLosResultados = juegaDAO.readAll();
        Map<Integer, FilaClasificacion> tabla = new HashMap<>();

        for (Participa p : inscripciones) {
            tabla.put(p.getEquipo().getIdEquipo(), new FilaClasificacion(
                p.getEquipo().getNombre(), p.getGrupo() != null ? p.getGrupo() : "S/G"));
        }

        for (Juega j : todosLosResultados) {
            if (j.getPartido().getTorneo().getIdTorneo() == id) {
                FilaClasificacion fila = tabla.get(j.getEquipo().getIdEquipo());
                if (fila != null) {
                    fila.sumarGoles(j.getGoles());
                    fila.sumarPuntos(j.getPuntos());
                    if (j.getPartido().getResultado() != null) fila.incrementarPJ();
                }
            }
        }
        return new ArrayList<>(tabla.values());
    }

    // Métodos de persistencia usados por GestionTorneo
    public void actualizarTorneo(Torneo t) { torneoDAO.update(t); }
    public void crearTorneo(Torneo t) { torneoDAO.create(t); }

    // Clase DTO
    public static class FilaClasificacion {
        private String nombreEquipo;
        private String grupo;
        private int pj = 0, puntos = 0, gf = 0;
        public FilaClasificacion(String n, String g) { this.nombreEquipo = n; this.grupo = g; }
        public void sumarGoles(int g) { this.gf += g; }
        public void sumarPuntos(int p) { this.puntos += p; }
        public void incrementarPJ() { this.pj++; }
        public String getNombreEquipo() { return nombreEquipo; }
        public String getGrupo() { return grupo; }
        public int getPj() { return pj; }
        public int getPuntos() { return puntos; }
        public int getGf() { return gf; }
    }
}