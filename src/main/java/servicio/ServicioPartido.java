package servicio;

import dao.PartidoDAO;
import modelo.Partido;
import java.util.List;

// Importaciones para Spring Boot
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/partidos")
public class ServicioPartido {

    private PartidoDAO partidoDAO;

    public ServicioPartido() {
        this.partidoDAO = new PartidoDAO();
    }

    // --- MÉTODOS PARA EL MÓVIL (API REST) ---

    /**
     * Endpoint que consume Android para obtener el calendario de un torneo.
     * GET /api/partidos/torneo/{idTorneo}
     */
    @GetMapping(value = "/torneo/{idTorneo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Partido> listarPorTorneoParaMovil(@PathVariable int idTorneo) {
        return listarPartidosPorTorneo(idTorneo);
    }

    /**
     * Endpoint para obtener un partido individual.
     * GET /api/partidos/{id}
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Partido> obtenerPartidoParaMovil(@PathVariable int id) {
        Partido p = obtenerPartido(id);
        if (p != null) {
            return ResponseEntity.ok(p);
        }
        return ResponseEntity.notFound().build();
    }

    // --- LÓGICA DE NEGOCIO ---

    public void crearPartido(Partido partido) {
        partidoDAO.create(partido);
    }

    public Partido obtenerPartido(int id) {
        return partidoDAO.read(id);
    }

    public List<Partido> listarPartidos() {
        return partidoDAO.readAll();
    }

    /**
     * ✅ MEJORADO: Ahora usa el método específico del DAO.
     * Ya no filtramos manualmente, dejamos que la BD haga el trabajo.
     */
    public List<Partido> listarPartidosPorTorneo(int idTorneo) {
        return partidoDAO.findPartidosByTorneo(idTorneo);
    }

    public void actualizarPartido(Partido partido) {
        partidoDAO.update(partido);
    }

    public void eliminarPartido(Partido partido) {
        partidoDAO.delete(partido);
    }
}