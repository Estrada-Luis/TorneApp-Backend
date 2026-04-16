package servicio;

import dao.DAO;
import java.util.List;
import modelo.Club;
import modelo.Equipo;
import modelo.Torneo;
import modelo.Federacion; // Importante añadir esta

// Importaciones necesarias para que funcione la conexión con el móvil
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController 
@RequestMapping("/api/clubes") 
public class ServicioClub {

    private DAO<Club> clubDAO = new DAO<>(Club.class);
    private DAO<Federacion> fedDAO = new DAO<>(Federacion.class); // Necesario para buscar la federación

    // --- MÉTODO PARA EL MÓVIL (CORREGIDO) ---
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            // --- PARCHE DE SEGURIDAD PARA EL ID DE FEDERACIÓN ---
            // Si el móvil manda ID 0 o nulo, le asignamos la Federación 1 nosotros
            if (club.getFederacion() == null || club.getFederacion().getIdFederacion() == 0) {
                // Buscamos la federación 1 en la base de datos
                Federacion fedFija = fedDAO.read(1); 
                
                // Si por lo que sea no la encuentra, la creamos de emergencia
                if (fedFija == null) {
                    fedFija = new Federacion();
                    fedFija.setIdFederacion(1);
                }
                club.setFederacion(fedFija);
            }
            // ---------------------------------------------------

            crearClub(club); 
            return ResponseEntity.ok("Club guardado con éxito");
        } catch (Exception e) {
            e.printStackTrace(); // Esto sacará el error detallado en el log de Render
            return ResponseEntity.status(500).body("Error al guardar: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE CONSULTA PARA EL MÓVIL ---
    @GetMapping("/listar")
    public List<Club> listarParaMovil() {
        return listarClubes();
    }

    // --- TU LÓGICA DE SIEMPRE ---
    
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
