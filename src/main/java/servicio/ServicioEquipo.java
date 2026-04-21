package servicio;

import dao.EquipoDAO;
import dao.UsuarioDAO; // Necesitarás este DAO si quieres buscar jugadores directamente
import modelo.Equipo;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/equipos")
public class ServicioEquipo {

    private EquipoDAO equipoDAO;
    private UsuarioDAO usuarioDAO; // Añadido para gestionar los jugadores

    public ServicioEquipo() {
        this.equipoDAO = new EquipoDAO();
        this.usuarioDAO = new UsuarioDAO(); // Inicializado
    }

    // --- MÉTODOS PARA EL MÓVIL ---

    @GetMapping("/club/{idClub}")
    public List<Equipo> listarPorClubParaMovil(@PathVariable int idClub) {
        return listarEquiposPorClub(idClub);
    }

    /**
     * ✅ NUEVO: Este es el método que llamará la "ListaJugadoresActivity"
     * Devuelve todos los usuarios que tienen asignado este equipo.
     */
    @GetMapping("/{idEquipo}/jugadores")
    public List<Usuario> listarJugadoresDeEquipo(@PathVariable int idEquipo) {
        List<Usuario> jugadores = new ArrayList<>();
        // Recorremos todos los usuarios y filtramos por equipo
        // (Esto es lo más sencillo si no quieres tocar el DAO)
        for (Usuario u : usuarioDAO.readAll()) {
            if (u.getEquipo() != null && u.getEquipo().getIdEquipo() == idEquipo) {
                jugadores.add(u);
            }
        }
        return jugadores;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Equipo> registrarDesdeMovil(@RequestBody Equipo equipo) {
        try {
            crearEquipo(equipo); 
            return ResponseEntity.ok(equipo);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --- LÓGICA ORIGINAL ---

    public void crearEquipo(Equipo equipo) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < 4) {
            sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        equipo.setCodigoVinculacion(sb.toString());
        
        equipoDAO.create(equipo);
    }

    public List<Equipo> listarEquipos() {
        return equipoDAO.readAll();
    }

    public List<Equipo> listarEquiposPorClub(int idClub) {
        List<Equipo> filtrados = new ArrayList<>();
        for (Equipo e : equipoDAO.readAll()) {
            if (e.getClub() != null && e.getClub().getIdClub() == idClub) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    public Equipo obtenerEquipo(int idEquipo) {
        return equipoDAO.read(idEquipo);
    }

    public void eliminarEquipo(int idEquipo) {
        Equipo equipo = equipoDAO.read(idEquipo);
        if (equipo != null) {
            equipoDAO.delete(equipo);
        }
    }

    public void actualizarEquipo(Equipo equipo) {
        equipoDAO.update(equipo);
    }
}