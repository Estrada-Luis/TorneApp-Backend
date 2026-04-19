package servicio;

import dao.TorneoDAO;
import modelo.Torneo;
import java.util.List;

// Importaciones para Spring Boot
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/torneos")
public class ServicioTorneo {

    private TorneoDAO torneoDAO;

    public ServicioTorneo() {
        this.torneoDAO = new TorneoDAO();
    }

    // --- MÉTODOS PARA EL MÓVIL ---

    // El móvil pide todos los torneos (ej: api/torneos/listar)
    @GetMapping("/listar")
    public List<Torneo> listarParaMovil() {
        return listarTorneos();
    }

    // El móvil pide detalles de un torneo y cuántos inscritos lleva
    @GetMapping("/{id}")
    public ResponseEntity<Torneo> obtenerTorneoParaMovil(@PathVariable int id) {
        Torneo t = buscarTorneoPorId(id);
        if (t != null) {
            // Podríamos meter el conteo de inscritos en un campo transitorio si fuera necesario
            return ResponseEntity.ok(t);
        }
        return ResponseEntity.notFound().build();
    }

    // --- TU LÓGICA ORIGINAL (Respetada al 100%) ---

    public Torneo crearTorneo(Torneo torneo) {
        torneoDAO.create(torneo);
        return torneo;
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