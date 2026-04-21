package servicio;

import dao.TorneoDAO;
import modelo.Torneo;
import java.util.List;
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
    private final String UPLOAD_DIR = "uploads/pdfs/";

    public ServicioTorneo() {
        this.torneoDAO = new TorneoDAO();
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
        }
    }

    // --- NUEVOS MÉTODOS PARA EL HISTORIAL (MÓVIL) ---

    /**
     * Devuelve los torneos donde algún equipo del club está inscrito.
     */
    @GetMapping("/inscritos/club/{id}")
    public List<Torneo> listarTorneosInscritos(@PathVariable int id) {
        return torneoDAO.findInscritosByClub(id);
    }

    /**
     * Devuelve los torneos creados/organizados por el club.
     */
    @GetMapping("/organizados/club/{id}")
    public List<Torneo> listarTorneosOrganizados(@PathVariable int id) {
        return torneoDAO.findOrganizadosByClub(id);
    }

    // --- MÉTODOS EXISTENTES: CREAR CON PDF ---

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

    // --- MÉTODOS EXISTENTES: DESCARGAR/VER PDF ---

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    // --- MÉTODOS DE COMPATIBILIDAD Y LÓGICA ---

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

    public void crearTorneo(Torneo torneo) {
        torneoDAO.create(torneo);
    }

    public void actualizarTorneo(Torneo torneo) {
        torneoDAO.update(torneo);
    }

    public Torneo buscarTorneoPorId(int id) {
        return torneoDAO.read(id);
    }

    public List<Torneo> listarTorneos() {
        return torneoDAO.readAll();
    }

    public void eliminarTorneo(Torneo torneo) {
        torneoDAO.delete(torneo);
    }

    public int contarInscritos(int idTorneo) {
        return torneoDAO.countInscripciones(idTorneo);
    }
}