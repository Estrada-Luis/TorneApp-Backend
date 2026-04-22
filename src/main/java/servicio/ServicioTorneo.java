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

// Importaciones Spring
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@RestController
@RequestMapping("/api/torneos")
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

    // --- 🚀 MÉTODOS DE INSCRIPCIÓN (Para Android) ---

    @PostMapping("/{idTorneo}/inscribir/{idEquipo}")
    public ResponseEntity<Void> inscribirEquipo(@PathVariable int idTorneo, @PathVariable int idEquipo) {
        try {
            System.out.println("Solicitud de inscripción -> Torneo: " + idTorneo + ", Equipo: " + idEquipo);
            
            Torneo t = torneoDAO.read(idTorneo);
            Equipo e = equipoDAO.read(idEquipo);

            if (t == null || e == null) return ResponseEntity.notFound().build();

            Participa nuevaInscripcion = new Participa();
            nuevaInscripcion.setTorneo(t);
            nuevaInscripcion.setEquipo(e);
            nuevaInscripcion.setGrupo("S/G");
            
            // Inicializamos la clave compuesta necesaria para tu modelo Participa
            ParticipaId idCompuesta = new ParticipaId(e.getIdEquipo(), t.getIdTorneo());
            nuevaInscripcion.setId(idCompuesta);

            participaDAO.create(nuevaInscripcion);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 📋 MÉTODOS DE HISTORIAL (Para Gestión Activa) ---

    @GetMapping("/inscritos/club/{id}")
    public List<Torneo> listarTorneosInscritos(@PathVariable int id) {
        System.out.println("Backend: Buscando torneos inscritos para club ID: " + id);
        // Este método en TorneoDAO debe buscar en la tabla 'participa'
        return torneoDAO.findInscritosByClub(id);
    }

    @GetMapping("/organizados/club/{id}")
    public List<Torneo> listarTorneosOrganizados(@PathVariable int id) {
        return torneoDAO.findOrganizadosByClub(id);
    }

    // --- 🏆 MÓDULO DE CLASIFICACIÓN ---

    @GetMapping("/{idTorneo}/clasificacion")
    public List<FilaClasificacion> obtenerClasificacion(@PathVariable int idTorneo) {
        List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(idTorneo);
        List<Juega> todosLosResultados = juegaDAO.readAll();
        Map<Integer, FilaClasificacion> tabla = new HashMap<>();

        for (Participa p : inscripciones) {
            tabla.put(p.getEquipo().getIdEquipo(), new FilaClasificacion(
                p.getEquipo().getNombre(), p.getGrupo() != null ? p.getGrupo() : "S/G"));
        }

        for (Juega j : todosLosResultados) {
            if (j.getPartido().getTorneo().getIdTorneo() == idTorneo) {
                FilaClasificacion fila = tabla.get(j.getEquipo().getIdEquipo());
                if (fila != null) {
                    fila.sumarGoles(j.getGoles());
                    fila.sumarPuntos(j.getPuntos());
                    if (j.getPartido().getResultado() != null) fila.incrementarPJ();
                }
            }
        }

        return tabla.values().stream()
                .sorted((f1, f2) -> f2.getPuntos() != f1.getPuntos() ? 
                        Integer.compare(f2.getPuntos(), f1.getPuntos()) : 
                        Integer.compare(f2.getGf(), f1.getGf()))
                .collect(Collectors.toList());
    }

    // --- 🔍 MÉTODOS DE LECTURA Y COMPATIBILIDAD ---

    @GetMapping("/listar")
    public List<Torneo> listarParaMovil() {
        return torneoDAO.readAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneo> obtenerTorneoParaMovil(@PathVariable int id) {
        Torneo t = torneoDAO.read(id);
        return (t != null) ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    // --- 🛠️ MÉTODOS DE PERSISTENCIA (Usados por GestionTorneo) ---

    public void actualizarTorneo(Torneo t) {
        torneoDAO.update(t);
    }

    public void crearTorneo(Torneo t) {
        torneoDAO.create(t);
    }

    // --- 📂 GESTIÓN DE ARCHIVOS ---

    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Torneo> crearTorneoConArchivo(
            @RequestPart("torneo") Torneo torneo,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        try {
            if (archivo != null && !archivo.isEmpty()) {
                String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                Path rutaDestino = Paths.get(UPLOAD_DIR).resolve(nombreArchivo);
                Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
                torneo.setPdfUrl(nombreArchivo);
            }
            torneoDAO.create(torneo);
            return ResponseEntity.ok(torneo);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- CLASE DTO PARA CLASIFICACIÓN ---
    public static class FilaClasificacion {
        private String nombreEquipo;
        private String grupo;
        private int pj = 0;
        private int puntos = 0;
        private int gf = 0;
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