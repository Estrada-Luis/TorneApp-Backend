package servicio;

import dao.JuegaDAO;
import modelo.Equipo;
import modelo.Juega;
import modelo.JuegaId;
import modelo.Partido;
import dao.PartidoDAO;

import java.util.ArrayList;
import java.util.List;

// Importaciones para la API
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/juega")
public class ServicioJuega {

    private JuegaDAO juegaDAO;
    private PartidoDAO partidoDAO; // Necesario para actualizar el String del resultado en Partido

    public ServicioJuega() {
        this.juegaDAO = new JuegaDAO();
        this.partidoDAO = new PartidoDAO();
    }

    // --- MÉTODOS PARA EL MÓVIL ---

    /**
     * Endpoint para actualizar los goles y puntos de un partido.
     * Se llama desde el diálogo de Android.
     */
    @PostMapping("/actualizar-resultado")
    public ResponseEntity<String> actualizarResultado(
            @RequestParam int idPartido, 
            @RequestParam int golesLocal, 
            @RequestParam int golesVisitante) {
        
        try {
            List<Juega> participaciones = listarJuegaPorPartido(idPartido);
            
            if (participaciones.size() < 2) {
                return ResponseEntity.badRequest().body("El partido no tiene dos equipos asignados.");
            }

            // Asumiendo que el primero es Local y el segundo Visitante
            Juega jLocal = participaciones.get(0);
            Juega jVisitante = participaciones.get(1);

            // 1. Asignar goles
            jLocal.setGoles(golesLocal);
            jVisitante.setGoles(golesVisitante);

            // 2. Lógica de puntos para la clasificación
            if (golesLocal > golesVisitante) {
                jLocal.setPuntos(3);
                jVisitante.setPuntos(0);
            } else if (golesLocal < golesVisitante) {
                jLocal.setPuntos(0);
                jVisitante.setPuntos(3);
            } else {
                jLocal.setPuntos(1);
                jVisitante.setPuntos(1);
            }

            // 3. Persistir cambios en la tabla Juega
            juegaDAO.update(jLocal);
            juegaDAO.update(jVisitante);

            // 4. Actualizar el String de resultado en la tabla Partido para que el móvil lo vea
            Partido p = jLocal.getPartido();
            p.setResultado(golesLocal + " - " + golesVisitante);
            partidoDAO.update(p);

            return ResponseEntity.ok("Resultado actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/partido/{idPartido}")
    public List<Equipo> listarEquiposPorPartidoParaMovil(@PathVariable int idPartido) {
        return obtenerEquiposPorPartido(idPartido);
    }

    // --- LÓGICA DE NEGOCIO ---

    public void añadirEquipoAPartido(Equipo equipo, Partido partido) {
        JuegaId id = new JuegaId(equipo.getIdEquipo(), partido.getIdPartido());
        Juega juega = new Juega();
        juega.setId(id);
        juega.setEquipo(equipo);
        juega.setPartido(partido);
        juegaDAO.create(juega);
    }

    /**
     * Obtiene los objetos Juega (con goles y puntos) de un partido.
     */
    public List<Juega> listarJuegaPorPartido(int idPartido) {
        List<Juega> lista = new ArrayList<>();
        for (Juega j : juegaDAO.readAll()) {
            if (j.getPartido().getIdPartido() == idPartido) {
                lista.add(j);
            }
        }
        return lista;
    }

    public List<Equipo> obtenerEquiposPorPartido(int idPartido) {
        List<Equipo> equipos = new ArrayList<>();
        for (Juega j : juegaDAO.readAll()) {
            if (j.getPartido().getIdPartido() == idPartido) {
                equipos.add(j.getEquipo());
            }
        }
        return equipos;
    }

    public void eliminar(Juega juega) {
        juegaDAO.delete(juega);
    }
}