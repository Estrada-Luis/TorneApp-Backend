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

    /**
     * ✅ MÉTODO PARA ESCRITORIO: Crear torneo directamente
     */
    public void crearTorneo(Torneo t) {
        torneoDAO.create(t);
    }

    /**
     * ✅ CREAR UN NUEVO TORNEO (Desde API/Móvil)
     */
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

    // --- ⚽ GESTIÓN DE PARTIDOS Y ACTAS (CON BLINDAJE) ---

    @GetMapping("/partidos/torneo/{id}")
    public List<Partido> listarPartidosPorTorneo(@PathVariable int id) {
        return juegaDAO.getPartidosPorTorneo(id);
    }

    @PostMapping("/partidos/actualizar-resultado")
    public ResponseEntity<Void> actualizarResultado(@RequestParam int idPartido, @RequestParam int golesLocal, @RequestParam int golesVisitante) {
        try {
            // 1. Buscamos el partido para comprobar su estado
            Partido p = torneoDAO.getPartidoById(idPartido);
            
            // 🔒 CAMBIO SEGURIDAD: Si el acta está cerrada, bloqueamos cualquier edición
            if (p != null && p.isActaCerrada()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
            }

            // 2. Actualizamos los goles en la tabla 'juega' (Lógica de puntos/clasificación)
            juegaDAO.actualizarResultado(idPartido, golesLocal, golesVisitante);
            
            // 3. Sincronizamos el String 'resultado' en la tabla 'partido'
            if (p != null) {
                p.setResultado(golesLocal + "-" + golesVisitante);
                torneoDAO.updatePartido(p);
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/torneos/{id}/generar-eliminatorias")
    public ResponseEntity<Void> generarFaseFinal(@PathVariable int id) {
        // Aquí iría la lógica de cruces automáticos
        System.out.println("Generando eliminatorias para torneo: " + id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/torneos/asignar-grupo")
    public void asignarGrupo(@RequestParam int idEquipo, @RequestParam int idTorneo, @RequestParam String grupo) {
        participaDAO.actualizarGrupo(idEquipo, idTorneo, grupo);
    }

    // --- 📋 CONSULTAS GENERALES ---

    @GetMapping("/torneos/listar")
    public List<Torneo> listarParaMovil() { 
        return torneoDAO.readAll(); 
    }

    @GetMapping("/equipos/torneo/{id}")
    public List<Equipo> listarEquiposPorTorneo(@PathVariable int id) { 
        return participaDAO.getEquiposPorTorneo(id); 
    }

    public void actualizarTorneo(Torneo t) { 
        torneoDAO.update(t); 
    }

    @GetMapping("/torneos/{id}/clasificacion")
    public List<FilaClasificacion> obtenerClasificacion(@PathVariable int id) {
        // 1. Obtenemos los equipos que participan en este torneo específico
        List<Participa> inscripciones = participaDAO.getParticipacionesPorTorneo(id);
        
        // 2. Corregido el espacio en el nombre de la variable
        List<Juega> todosLosResultados = juegaDAO.readAll();
        
        Map<Integer, FilaClasificacion> tabla = new HashMap<>();
        
        // Inicializamos la tabla con los equipos inscritos
        for (Participa p : inscripciones) {
            if (p.getEquipo() != null) {
                tabla.put(p.getEquipo().getIdEquipo(), 
                    new FilaClasificacion(p.getEquipo().getNombre(), p.getGrupo()));
            }
        }
        
        // 3. Procesamos los resultados para sumar puntos y goles
        for (Juega j : todosLosResultados) {
            // Solo sumamos si el partido pertenece a este torneo
            if (j.getPartido() != null && j.getPartido().getTorneo().getIdTorneo() == id) {
                FilaClasificacion fila = tabla.get(j.getEquipo().getIdEquipo());
                
                if (fila != null) {
                    fila.sumarGoles(j.getGoles());
                    fila.sumarPuntos(j.getPuntos());
                    
                    // Consideramos partido jugado si el resultado no es nulo o si el acta está cerrada
                    if (j.getPartido().getResultado() != null || j.getPartido().isActaCerrada()) {
                        // Como en 'Juega' hay dos registros por partido (uno por equipo), 
                        // esta lógica sumará 1 PJ a cada uno correctamente.
                        fila.incrementarPJ();
                    }
                }
            }
        }
        
        // Devolvemos la lista de valores de la tabla
        return new ArrayList<>(tabla.values());
    }

    // --- CLASE INTERNA PARA CLASIFICACIÓN ---
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