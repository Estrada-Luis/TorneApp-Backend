package servicio;

import dao.PartidoDAO;
import modelo.Partido;

import java.util.ArrayList;
import java.util.List;

// Importaciones para Spring Boot
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/partidos")
public class ServicioPartido {

    private PartidoDAO partidoDAO;

    public ServicioPartido() {
        this.partidoDAO = new PartidoDAO();
    }

    // --- MÉTODOS PARA EL MÓVIL ---

    // El móvil pide el calendario de un torneo (ej: api/partidos/torneo/1)
    @GetMapping("/torneo/{idTorneo}")
    public List<Partido> listarPorTorneoParaMovil(@PathVariable int idTorneo) {
        return listarPartidosPorTorneo(idTorneo);
    }

    // El móvil puede pedir los detalles de un partido concreto
    @GetMapping("/{id}")
    public Partido obtenerPartidoParaMovil(@PathVariable int id) {
        return obtenerPartido(id);
    }

    // --- TU LÓGICA ORIGINAL (Respetada al 100%) ---

    public void crearPartido(Partido partido) {
        partidoDAO.create(partido);
    }

    public Partido obtenerPartido(int id) {
        return partidoDAO.read(id);
    }

    public List<Partido> listarPartidos() {
        return partidoDAO.readAll();
    }

    public List<Partido> listarPartidosPorTorneo(int idTorneo) {
        List<Partido> partidos = new ArrayList<>();

        for (Partido p : partidoDAO.readAll()) {
            if (p.getTorneo().getIdTorneo() == idTorneo) {
                partidos.add(p);
            }
        }
        return partidos;
    }

    public void actualizarPartido(Partido partido) {
        partidoDAO.update(partido);
    }

    public void eliminarPartido(Partido partido) {
        partidoDAO.delete(partido);
    }
}