package servicio;

import dao.EquipoDAO;
import modelo.Equipo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Importaciones para Spring
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/equipos")
public class ServicioEquipo {

    private EquipoDAO equipoDAO;

    public ServicioEquipo() {
        this.equipoDAO = new EquipoDAO();
    }

    // --- MÉTODOS PARA EL MÓVIL ---

    // El móvil puede pedir todos los equipos de un Club concreto
    @GetMapping("/club/{idClub}")
    public List<Equipo> listarPorClubParaMovil(@PathVariable int idClub) {
        return listarEquiposPorClub(idClub);
    }

    // El móvil puede registrar un equipo nuevo
    @PostMapping("/registrar")
    public ResponseEntity<Equipo> registrarDesdeMovil(@RequestBody Equipo equipo) {
        try {
            crearEquipo(equipo); // Esto genera el código y guarda en BD
            return ResponseEntity.ok(equipo);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --- TU LÓGICA ORIGINAL (No se toca) ---

    public void crearEquipo(Equipo equipo) {
        // Generación del código alfanumérico de 4 caracteres (ej: CP3A)
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