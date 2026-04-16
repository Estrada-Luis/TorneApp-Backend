package servicio;

import dao.DAO;
import java.util.List;
import modelo.Club;
import modelo.Equipo;
import modelo.Torneo;

// Importaciones necesarias para que funcione la conexión con el móvil
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController // Esto le dice a Spring que esta clase atiende peticiones (como las de Retrofit)
@RequestMapping("/api/clubes") // Esta es la ruta base que usaremos en el móvil
public class ServicioClub {

    private DAO<Club> clubDAO = new DAO<>(Club.class);

    // --- MÉTODO PARA EL MÓVIL ---
    // Cuando el móvil haga un POST a /api/clubes/registrar, se ejecutará esto
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            crearClub(club); // Llama a tu método de siempre
            return ResponseEntity.ok("Club guardado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE CONSULTA PARA EL MÓVIL (Opcionales por si los necesitas) ---
    @GetMapping("/listar")
    public List<Club> listarParaMovil() {
        return listarClubes();
    }

    // --- TU LÓGICA DE SIEMPRE (No tocamos nada de lo que ya tenías) ---
    
    public void crearClub(Club club) {
        clubDAO.create(club);
    }

    public void actualizarClub(Club club) {
        clubDAO.update(club);
    }

    public void eliminarClub(Club club) {
        clubDAO.delete(club);
    }

    public List<Club> listarClubes() {
        return clubDAO.readAll();
    }

    public Club buscarPorNombre(String nombre) {
        List<Club> lista = clubDAO.readAll();
        if (lista == null) return null;
        
        return lista.stream()
                .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public int obtenerTotalClubes() {
        List<Club> lista = clubDAO.readAll();
        return (lista != null) ? lista.size() : 0;
    }

    public void inscribirEquipo(Equipo e, Torneo t) {
        System.out.println("Equipo " + e.getNombre() + " inscrito en " + t.getNombre());
    }
}