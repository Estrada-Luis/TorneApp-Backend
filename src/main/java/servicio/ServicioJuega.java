package servicio;

import dao.JuegaDAO;
import modelo.Equipo;
import modelo.Juega;
import modelo.JuegaId;
import modelo.Partido;

import java.util.ArrayList;
import java.util.List;

// Importaciones para la API
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/juega")
public class ServicioJuega {

    private JuegaDAO juegaDAO;

    public ServicioJuega() {
        this.juegaDAO = new JuegaDAO();
    }

    // --- MÉTODO PARA EL MÓVIL ---
    // Permite que el móvil obtenga los dos equipos de un partido (Ej: api/juega/partido/5)
    @GetMapping("/partido/{idPartido}")
    public List<Equipo> listarEquiposPorPartidoParaMovil(@PathVariable int idPartido) {
        return obtenerEquiposPorPartido(idPartido);
    }

    // --- TU LÓGICA ORIGINAL (No se toca) ---

    public void añadirEquipoAPartido(Equipo equipo, Partido partido) {
        JuegaId id = new JuegaId(
                equipo.getIdEquipo(),
                partido.getIdPartido()
        );

        Juega juega = new Juega();
        juega.setId(id);
        juega.setEquipo(equipo);
        juega.setPartido(partido);

        juegaDAO.create(juega);
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