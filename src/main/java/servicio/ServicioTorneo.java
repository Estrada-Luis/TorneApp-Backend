package servicio;

import dao.*;
import modelo.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@RestController
@RequestMapping("/api")
public class ServicioTorneo {

    private TorneoDAO torneoDAO = new TorneoDAO();
    private ParticipaDAO participaDAO = new ParticipaDAO();
    private JuegaDAO juegaDAO = new JuegaDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();
    private final String UPLOAD_DIR = "uploads/pdfs/";

    public ServicioTorneo() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
        }
    }

    // --- 🏆 GESTIÓN DE TORNEOS (Escritorio y Móvil) ---

    @GetMapping("/torneos/listar")
    public List<Torneo> listarParaMovil() { 
        return torneoDAO.readAll(); 
    }

    @GetMapping("/torneos/{id}")
    public ResponseEntity<Torneo> obtenerTorneoPorId(@PathVariable int id) {
        Torneo t = torneoDAO.read(id);
        return (t != null) ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    // Para el escritorio (método directo)
    public void crearTorneo(Torneo t) {
        torneoDAO.create(t);
    }

    @PostMapping("/torneos/crear/{id}")
    public ResponseEntity<Torneo> crearTorneoDesdeMovil(@RequestBody Torneo torneo, @PathVariable int id) {
        try {
            Club club = new ClubDAO().read(id);
            if (club != null) {
                torneo.setClubOrganizador(club);
                torneo.setFederacionOrganizador(club.getFederacion());
                torneo.setEstado("PENDIENTE");
            } else {
                Federacion fed = new FederacionDAO().read(id);
                if (fed != null) {
                    torneo.setFederacionOrganizador(fed);
                    torneo.setEstado("PROXIMO"); 
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
            torneoDAO.create(torneo);
            return ResponseEntity.ok(torneo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/torneos/actualizar")
    public ResponseEntity<Void> actualizarTorneo(@RequestBody Torneo t) {
        try {
            torneoDAO.update(t);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- ⚽ EQUIPOS E INSCRIPCIONES ---

 // ✅ ASÍ DEBE QUEDAR EN ECLIPSE (Sustituye el anterior)
    @PostMapping("/torneos/inscribir") // <-- QUITA LAS LLAVES DE AQUÍ
    public ResponseEntity<Void> inscribirEquipo(@RequestParam int idTorneo, @RequestParam int idEquipo) {
        try {
            participaDAO.inscribir(idTorneo, idEquipo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); // Esto te ayudará a ver errores en el log de Render
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/equipos/torneo/{id}")
    public List<Equipo> listarEquiposPorTorneo(@PathVariable int id) { 
        return participaDAO.getEquiposPorTorneo(id); 
    }

    @PostMapping("/torneos/asignar-grupo")
    public void asignarGrupo(@RequestParam int idEquipo, @RequestParam int idTorneo, @RequestParam String grupo) {
        participaDAO.actualizarGrupo(idEquipo, idTorneo, grupo);
    }

    // --- 🏢 FILTRADO PARA "MIS COMPETICIONES" (MÓVIL) ---

    @GetMapping("/torneos/inscritos/club/{id}")
    public List<Torneo> listarTorneosInscritosPorClub(@PathVariable int id) {
        return torneoDAO.listarInscritosPorClub(id);
    }

    @GetMapping("/torneos/organizados/club/{id}")
    public List<Torneo> listarTorneosOrganizadosPorClub(@PathVariable int id) {
        return torneoDAO.listarOrganizadosPorClub(id);
    }

    // --- 🏁 FASE FINAL Y PARTIDOS ---

    @PostMapping("/torneos/{id}/generar-eliminatorias")
    public ResponseEntity<Void> generarFaseFinal(@PathVariable int id) {
        try {
            // Asegúrate de que este método exista en tu TorneoDAO
            torneoDAO.generarFaseFinal(id); 
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/partidos/torneo/{id}")
    public List<Partido> listarPartidosPorTorneo(@PathVariable int id) {
        return juegaDAO.getPartidosPorTorneo(id);
    }

    @PostMapping("/partidos/actualizar-resultado")
    public ResponseEntity<Void> actualizarResultado(@RequestParam int idPartido, @RequestParam int golesLocal, @RequestParam int golesVisitante) {
        try {
            Partido p = torneoDAO.getPartidoById(idPartido);
            if (p != null && p.isActaCerrada()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
            }
            juegaDAO.actualizarResultado(idPartido, golesLocal, golesVisitante);
            if (p != null) {
                p.setResultado(golesLocal + "-" + golesVisitante);
                torneoDAO.updatePartido(p);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 📊 CLASIFICACIÓN ---

    @GetMapping("/torneos/{id}/clasificacion")
    public List<FilaClasificacion> obtenerClasificacion(@PathVariable int id) {
        List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(id);
        List<Juega> todosLosResultados = juegaDAO.readAll();
        Map<Integer, FilaClasificacion> tabla = new HashMap<>();
        
        for (Participa p : inscripciones) {
            if (p.getEquipo() != null) {
                tabla.put(p.getEquipo().getIdEquipo(), 
                    new FilaClasificacion(p.getEquipo().getNombre(), p.getGrupo()));
            }
        }
        
        for (Juega j : todosLosResultados) {
            if (j.getPartido() != null && j.getPartido().getTorneo().getIdTorneo() == id) {
                FilaClasificacion fila = tabla.get(j.getEquipo().getIdEquipo());
                if (fila != null) {
                    fila.sumarGoles(j.getGoles());
                    fila.sumarPuntos(j.getPuntos());
                    if (j.getPartido().getResultado() != null || j.getPartido().isActaCerrada()) {
                        fila.incrementarPJ();
                    }
                }
            }
        }
        return new ArrayList<>(tabla.values());
    }

    // --- 📄 GESTIÓN DE PDFs ---

    @GetMapping("/torneos/pdf/{filename:.+}")
    public ResponseEntity<Resource> descargarNormativa(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 📝 CLASE AUXILIAR ---

    public static class FilaClasificacion {
        private String nombreEquipo;
        private String grupo;
        private int pj = 0, puntos = 0, gf = 0;
        public FilaClasificacion(String n, String g) { 
            this.nombreEquipo = n; 
            this.grupo = (g != null) ? g : "S/G"; 
        }
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
