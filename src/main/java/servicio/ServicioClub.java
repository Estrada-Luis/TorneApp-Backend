package servicio;

import dao.DAO;
import java.util.List;
import modelo.Club;
import modelo.Equipo;
import modelo.Torneo;
import modelo.Federacion;
import modelo.Usuario;    
import modelo.RolUsuario; 

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController 
@RequestMapping("/api/clubes") 
public class ServicioClub {

    private DAO<Club> clubDAO = new DAO<>(Club.class);
    private DAO<Federacion> fedDAO = new DAO<>(Federacion.class);
    private DAO<Usuario> usuarioDAO = new DAO<>(Usuario.class); 

    /**
     * Registro desde el móvil.
     * Crea un Club y un Usuario vinculado para el login.
     */
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            // 1. Extraemos los datos del club enviado por el móvil
            // IMPORTANTE: Asegúrate de que en Club.java el método sea getCorreo()
            String nombreTxt = club.getNombre();
            String correoTxt = club.getCorreo(); 
            String passTxt = club.getPassword();

            // Validación básica para evitar NullPointerException
            if (correoTxt == null || passTxt == null) {
                return ResponseEntity.status(400).body("Error: Datos incompletos (email o password)");
            }

            // 2. Asignación de Federación (Parche para evitar error 500)
            if (club.getFederacion() == null || club.getFederacion().getIdFederacion() == 0) {
                Federacion fedFija = fedDAO.read(1); 
                if (fedFija == null) {
                    fedFija = new Federacion();
                    fedFija.setIdFederacion(1);
                }
                club.setFederacion(fedFija);
            }

            // 3. Guardar el Club en la base de datos
            clubDAO.create(club); 

            // 4. Crear el Usuario para que pueda hacer Login
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombreTxt); 
            nuevoUsuario.setEmail(correoTxt); // Usamos el setter de tu clase Usuario
            nuevoUsuario.setPassword(passTxt);
            nuevoUsuario.setRol(RolUsuario.CLUB);
            
            // Sincronizamos el estado de validación inicial
            nuevoUsuario.setValidado(false);

            usuarioDAO.create(nuevoUsuario);

            System.out.println("[BACKEND] Registro exitoso: " + correoTxt);
            return ResponseEntity.ok("Registro completado con éxito.");
            
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE CONSULTA ---

    @GetMapping("/listar")
    public List<Club> listarParaMovil() {
        return clubDAO.readAll();
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody Club club) {
        try {
            clubDAO.update(club);
            return ResponseEntity.ok("Club actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar: " + e.getMessage());
        }
    }

    // --- MÉTODOS COMPLEMENTARIOS ---

    public void crearClub(Club club) {
        clubDAO.create(club);
    }

    public void actualizarClub(Club club) {
        clubDAO.update(club);
    }

    public void eliminarClub(Club club) {
        clubDAO.delete(club);
    }

    public Club buscarPorNombre(String nombre) {
        List<Club> lista = clubDAO.readAll();
        if (lista == null) return null;
        return lista.stream()
                .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
 // NO BORRES NADA, SOLO AÑADE ESTO AL FINAL DE LA CLASE
    public void inscribirEquipo(Equipo e, Torneo t) {
        // Aquí puedes añadir lógica en el futuro, por ahora 
        // lo dejamos para que el escritorio no dé error.
        System.out.println("Inscribiendo equipo: " + e.getNombre() + " en torneo: " + t.getNombre());
    }
}
