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

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            // 1. Extraemos los datos en variables String locales para asegurar el formato
            // Esto evita el error de modelo.Club@... que viste en la base de datos
            String nombreParaUsuario = String.valueOf(club.getNombre());
            String emailParaUsuario = String.valueOf(club.getCorreo());
            String passParaUsuario = String.valueOf(club.getPassword());

            // 2. Lógica de Federación (Seguridad para evitar el Error 500)
            if (club.getFederacion() == null || club.getFederacion().getIdFederacion() == 0) {
                Federacion fedFija = fedDAO.read(1); 
                if (fedFija == null) {
                    fedFija = new Federacion();
                    fedFija.setIdFederacion(1);
                }
                club.setFederacion(fedFija);
            }

            // 3. PRIMER GUARDADO: Tabla 'club'
            // Esto es lo que ya te funcionaba antes
            clubDAO.create(club); 

            // 4. SEGUNDO GUARDADO: Tabla 'usuario'
            // Esto es lo que permite que el Login funcione
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombreParaUsuario);
            nuevoUsuario.setEmail(emailParaUsuario);
            nuevoUsuario.setPassword(passParaUsuario);
            nuevoUsuario.setRol(RolUsuario.CLUB);

            usuarioDAO.create(nuevoUsuario);

            return ResponseEntity.ok("Registro de Club y Usuario completado con éxito");
            
        } catch (Exception e) {
            e.printStackTrace(); 
            // Si hay error, Render te dirá aquí exactamente por qué
            return ResponseEntity.status(500).body("Error en el registro: " + e.getMessage());
        }
    }

    // --- Métodos de consulta ---
    @GetMapping("/listar")
    public List<Club> listarParaMovil() {
        return clubDAO.readAll();
    }

    public void crearClub(Club club) {
        clubDAO.create(club);
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
}
