package servicio;

import dao.TorneoDAO;
import dao.ParticipaDAO;
import dao.JuegaDAO;
import dao.EquipoDAO; // Asegúrate de tener este import
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
    private EquipoDAO equipoDAO; // Añadido para la inscripción
    private final String UPLOAD_DIR = "uploads/pdfs/";

    public ServicioTorneo() {
        this.torneoDAO = new TorneoDAO();
        this.participaDAO = new ParticipaDAO();
        this.juegaDAO = new JuegaDAO();
        this.equipoDAO = new EquipoDAO(); // Inicializado
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
        }
    }

    // --- 🚀 NOVEDAD: MÉTODO PARA INSCRIBIR EQUIPOS (Soluciona el Error 404) ---
    @PostMapping("/{idTorneo}/inscribir/{idEquipo}")
    public ResponseEntity<Void> inscribirEquipo(@PathVariable int idTorneo, @PathVariable int idEquipo) {
        try {
            System.out.println("Inscribiendo -> Torneo: " + idTorneo + " Equipo: " + idEquipo);
            
            Torneo t = torneoDAO.read(idTorneo);
            Equipo e = equipoDAO.read(idEquipo);

            if (t == null || e == null) {
                return ResponseEntity.notFound().build();
            }

            // 1. Crear el objeto de relación
            Participa nuevaInscripcion = new Participa();
            nuevaInscripcion.setTorneo(t);
            nuevaInscripcion.setEquipo(e);
            nuevaInscripcion.setGrupo("S/G"); // Valor por defecto para el grupo
            
            // 🔥 CRÍTICO: Inicializar la clave compuesta (ParticipaId)
            // Si no haces esto, JPA no sabe que los IDs de la clave son los del equipo y torneo
            ParticipaId idCompuesta = new ParticipaId(e.getIdEquipo(), t.getIdTorneo());
            nuevaInscripcion.setId(idCompuesta);

            // 2. Guardar en la base de datos
            participaDAO.create(nuevaInscripcion);

            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 🏆 ENDPOINT DE CLASIFICACIÓN ---
    @GetMapping("/{idTorneo}/clasificacion")
    public List<FilaClasificacion> obtenerClasificacion(@PathVariable int idTorneo) {
        List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(idTorneo);
        List<Juega> todosLosResultados = juegaDAO.readAll();

        Map<Integer, FilaClasificacion> tabla = new HashMap<>();

        for (Participa p : inscripciones) {
            tabla.put(p.getEquipo().getIdEquipo(), new FilaClasificacion(
                p.getEquipo().getNombre(),
                p.getGrupo() != null ? p.getGrupo() : "S/G"
            ));
        }

        for (Juega j : todosLosResultados) {
            if (j.getPartido().getTorneo().getIdTorneo() == idTorneo) {
                FilaClasificacion fila = tabla.get(j.getEquipo().getIdEquipo());
                if (fila != null) {
                    fila.sumarGoles(j.getGoles());
                    fila.sumarPuntos(j.getPuntos());
                    if (j.getPartido().getResultado() != null && !j.getPartido().getResultado().isEmpty()) {
                        fila.incrementarPJ();
                    }
                }
            }
        }

        return tabla.values().stream()
                .sorted((f1, f2) -> {
                    if (f2.getPuntos() != f1.getPuntos()) 
                        return Integer.compare(f2.getPuntos(), f1.getPuntos());
                    return Integer.compare(f2.getGf(), f1.getGf());
                })
                .collect(Collectors.toList());
    }

    // --- MÉTODOS PARA EL HISTORIAL ---
    @GetMapping("/inscritos/club/{id}")
    public List<Torneo> listarTorneosInscritos(@PathVariable int id) {
        return torneoDAO.findInscritosByClub(id);
    }

    @GetMapping("/organizados/club/{id}")
    public List<Torneo> listarTorneosOrganizados(@PathVariable int id) {
        return torneoDAO.findOrganizadosByClub(id);
    }

    // --- GESTIÓN DE PDF Y CREACIÓN ---
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

    @GetMapping("/ver-pdf/{nombre:.+}")
    public ResponseEntity<Resource> descargarPdf(@PathVariable String nombre) {
        try {
            Path ruta = Paths.get(UPLOAD_DIR).resolve(nombre);
            Resource recurso = new UrlResource(ruta.toUri());
            if (recurso.exists() || recurso.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ResponseEntity.notFound().build();
    }

    // --- MÉTODOS DE COMPATIBILIDAD ---
    @GetMapping("/listar")
    public List<Torneo> listarParaMovil() {
        return torneoDAO.readAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneo> obtenerTorneoParaMovil(@PathVariable int id) {
        Torneo t = torneoDAO.read(id);
        if (t != null) return ResponseEntity.ok(t);
        return ResponseEntity.notFound().build();
    }

    // --- CLASE DTO ---
    public static class FilaClasificacion {
        private String nombreEquipo;
        private String grupo;
        private int pj = 0;
        private int puntos = 0;
        private int gf = 0;

        public FilaClasificacion(String nombre, String grupo) {
            this.nombreEquipo = nombre;
            this.grupo = grupo;
        }

        public void sumarGoles(int goles) { this.gf += goles; }
        public void sumarPuntos(int pts) { this.puntos += pts; }
        public void incrementarPJ() { this.pj++; }

        public String getNombreEquipo() { return nombreEquipo; }
        public String getGrupo() { return grupo; }
        public int getPj() { return pj; }
        public int getPuntos() { return puntos; }
        public int getGf() { return gf; }
    }

    public void crearTorneo(Torneo t) { torneoDAO.create(t); }
    public void actualizarTorneo(Torneo t) { torneoDAO.update(t); }
    public void eliminarTorneo(Torneo t) { torneoDAO.delete(t); }
}