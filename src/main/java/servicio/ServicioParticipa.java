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

    // --- MÉTODOS PARA EL MÓVIL ---

    // El móvil consulta qué equipos están en un torneo (ej: api/participaciones/torneo/3)
    @GetMapping("/torneo/{idTorneo}")
    public List<Equipo> listarEquiposPorTorneoParaMovil(@PathVariable int idTorneo) {
        return obtenerEquiposPorTorneo(idTorneo);
    }

    // El móvil inscribe un equipo (le mandamos los objetos Equipo y Torneo en el JSON)
    @PostMapping("/inscribir")
    public ResponseEntity<String> inscribirDesdeMovil(@RequestBody Participa datos) {
        try {
            // Reutilizamos tu lógica de creación de ID compuesta y guardado
            inscribirEquipo(datos.getEquipo(), datos.getTorneo());
            return ResponseEntity.ok("Inscripción realizada con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la inscripción: " + e.getMessage());
        }
    }

    // --- TU LÓGICA ORIGINAL (Respetada al 100%) ---

    public void inscribirEquipo(Equipo equipo, Torneo torneo) {
        ParticipaId id = new ParticipaId(
                equipo.getIdEquipo(),
                torneo.getIdTorneo()
        );

        Participa participa = new Participa();
        participa.setId(id);
        participa.setEquipo(equipo);
        participa.setTorneo(torneo);

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