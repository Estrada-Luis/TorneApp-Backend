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

    // --- 🏆 GESTIÓN DE TORNEOS ---

    @GetMapping("/torneos/listar")
    public List<Torneo> listarParaMovil() { 
        return torneoDAO.listarTorneosVisibles(); 
    }

    @GetMapping("/torneos/{id}")
    public ResponseEntity<Torneo> obtenerTorneoPorId(@PathVariable int id) {
        Torneo t = torneoDAO.read(id);
        return (t != null) ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    public void crearTorneo(Torneo t) {
        torneoDAO.create(t);
    }

    @GetMapping("/torneos/buscar")
    public List<Torneo> buscarPorEstado(@RequestParam String estado) {
        return torneoDAO.buscarPorEstado(estado);
    }

    @PostMapping("/torneos/{id}/aprobar")
    public ResponseEntity<Void> aprobarTorneo(@PathVariable int id) {
        try {
            Torneo t = torneoDAO.read(id);
            if (t != null) {
                t.setEstado("PROXIMO"); 
                torneoDAO.update(t);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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

    @PostMapping("/torneos/inscribir")
    public ResponseEntity<Void> inscribirEquipo(@RequestParam int idTorneo, @RequestParam int idEquipo) {
        try {
            participaDAO.inscribir(idTorneo, idEquipo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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

    // --- 📅 CALENDARIO ---

    @PostMapping("/torneos/{id}/generar-calendario")
    public ResponseEntity<Void> generarCalendarioGrupos(@PathVariable int id) {
        try {
            List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(id);
            Map<String, List<Equipo>> grupos = new HashMap<>();
            
            for (Participa p : inscripciones) {
                String g = (p.getGrupo() != null) ? p.getGrupo() : "SIN_GRUPO";
                grupos.computeIfAbsent(g, k -> new ArrayList<>()).add(p.getEquipo());
            }
            
            for (String nombreGrupo : grupos.keySet()) {
                if (nombreGrupo.equals("SIN_GRUPO")) continue;
                List<Equipo> equipos = grupos.get(nombreGrupo);
                for (int i = 0; i < equipos.size(); i++) {
                    for (int j = i + 1; j < equipos.size(); j++) {
                        juegaDAO.crearPartidoFaseGrupos(id, equipos.get(i).getIdEquipo(), equipos.get(j).getIdEquipo(), nombreGrupo);
                    }
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 🏢 FILTRADO "MIS COMPETICIONES" ---

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
            juegaDAO.actualizarResultado(idPartido, golesLocal, golesVisitante);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/partidos/cerrar-acta")
    public ResponseEntity<Void> cerrarActa(@RequestParam int idPartido) {
        try {
            juegaDAO.cerrarActa(idPartido);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 📊 CLASIFICACIÓN (🚩 CORREGIDO E INFALIBLE) ---

    @GetMapping("/torneos/{id}/clasificacion")
    public List<FilaClasificacion> obtenerClasificacion(@PathVariable int id) {
        List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(id);
        List<Juega> resultadosTorneo = juegaDAO.getResultadosPorTorneo(id); 
        
        Map<Integer, FilaClasificacion> tablaMap = new HashMap<>();
        
        // 1. Inicializamos la tabla con todos los equipos que se inscribieron
        for (Participa p : inscripciones) {
            if (p.getEquipo() != null) {
                int idEq = p.getEquipo().getIdEquipo();
                tablaMap.put(idEq, new FilaClasificacion(p.getEquipo().getNombre(), p.getGrupo()));
            }
        }
        
        // 2. 🚩 LA CLAVE: Recorremos los registros de la tabla 'juega' para sumar puntos y goles
        for (Juega j : resultadosTorneo) {
            if (j.getEquipo() != null) {
                int idEq = j.getEquipo().getIdEquipo();
                FilaClasificacion fila = tablaMap.get(idEq);
                
                if (fila != null) {
                    fila.sumarGoles(j.getGoles());
                    fila.sumarPuntos(j.getPuntos());
                    
                    // Solo incrementamos PJ si el partido tiene un resultado o el acta está cerrada
                    if (j.getPartido() != null && (j.getPartido().getResultado() != null)) {
                        fila.incrementarPJ();
                    }
                }
            }
        }
        
        // 3. Convertimos a lista y ordenamos por Puntos y luego por Goles a Favor
        List<FilaClasificacion> listaOrdenada = new ArrayList<>(tablaMap.values());
        listaOrdenada.sort((f1, f2) -> {
            if (f1.getPuntos() != f2.getPuntos()) {
                return f2.getPuntos() - f1.getPuntos(); // Descendente por puntos
            }
            return f2.getGf() - f1.getGf(); // Desempate por goles a favor
        });
        
        return listaOrdenada;
    }

    // --- 📄 PDFS ---

    @GetMapping("/torneos/pdf/{filename}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

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