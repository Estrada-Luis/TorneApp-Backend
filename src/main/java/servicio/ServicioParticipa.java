package servicio;

import dao.ParticipaDAO;
import modelo.Equipo;
import modelo.Participa;
import modelo.ParticipaId;
import modelo.Torneo;

import java.util.ArrayList;
import java.util.List;

// Importaciones para que el móvil pueda conectar
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/participaciones")
public class ServicioParticipa {

    private ParticipaDAO participaDAO;

    public ServicioParticipa() {
        this.participaDAO = new ParticipaDAO();
    }

    // --- MÉTODOS NUEVOS PARA LA GESTIÓN (MÓVIL) ---

    /**
     * Permite al organizador asignar un equipo a un grupo (A, B, C...).
     * POST /api/participaciones/asignar-grupo
     */
    @PostMapping("/asignar-grupo")
    public ResponseEntity<String> asignarGrupo(
            @RequestParam int idEquipo, 
            @RequestParam int idTorneo, 
            @RequestParam String grupo) {
        try {
            ParticipaId id = new ParticipaId(idEquipo, idTorneo);
            Participa p = participaDAO.read(id); // Importante que tu DAO acepte ParticipaId
            
            if (p != null) {
                p.setGrupo(grupo);
                participaDAO.update(p);
                return ResponseEntity.ok("Equipo asignado al grupo " + grupo);
            }
            return ResponseEntity.status(404).body("No se encontró la inscripción.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * El móvil consulta qué inscripciones (con su grupo) hay en un torneo.
     * GET /api/participaciones/torneo/{idTorneo}
     */
    @GetMapping("/torneo/{idTorneo}")
    public List<Participa> listarParticipacionesPorTorneoParaMovil(@PathVariable int idTorneo) {
        List<Participa> todas = participaDAO.readAll();
        List<Participa> filtradas = new ArrayList<>();

        for (Participa p : todas) {
            if (p.getTorneo() != null && p.getTorneo().getIdTorneo() == idTorneo) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    // --- MÉTODOS DE INSCRIPCIÓN ---

    @PostMapping("/inscribir")
    public ResponseEntity<String> inscribirDesdeMovil(@RequestBody Participa datos) {
        try {
            inscribirEquipo(datos.getEquipo(), datos.getTorneo());
            return ResponseEntity.ok("Inscripción realizada con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la inscripción: " + e.getMessage());
        }
    }

    // --- LÓGICA ORIGINAL ---

    public void inscribirEquipo(Equipo equipo, Torneo torneo) {
        ParticipaId id = new ParticipaId(equipo.getIdEquipo(), torneo.getIdTorneo());
        Participa participa = new Participa();
        participa.setId(id);
        participa.setEquipo(equipo);
        participa.setTorneo(torneo);
        participa.setGrupo("SIN ASIGNAR"); // Valor por defecto

        participaDAO.create(participa);
    }

    public List<Equipo> obtenerEquiposPorTorneo(int idTorneo) {
        List<Participa> inscripciones = participaDAO.readAll();
        List<Equipo> equipos = new ArrayList<>();

        for (Participa p : inscripciones) {
            if (p.getTorneo().getIdTorneo() == idTorneo) {
                equipos.add(p.getEquipo());
            }
        }
        return equipos;
    }

    public void eliminar(Participa participa) {
        participaDAO.delete(participa);
    }
}