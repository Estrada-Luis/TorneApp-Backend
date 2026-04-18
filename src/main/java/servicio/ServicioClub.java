package servicio;

import dao.DAO;
import java.util.List;
import modelo.Club;
import modelo.Equipo;
import modelo.Torneo;
import modelo.Federacion;
import modelo.Usuario;    // Necesaria para el doble registro
import modelo.RolUsuario; // Necesaria para asignar el rol

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController 
@RequestMapping("/api/clubes") 
public class ServicioClub {

    private DAO<Club> clubDAO = new DAO<>(Club.class);
    private DAO<Federacion> fedDAO = new DAO<>(Federacion.class);
    // NUEVO: DAO para poder escribir en la tabla de usuarios
    private DAO<Usuario> usuarioDAO = new DAO<>(Usuario.class); 

    // --- MÉTODO PARA EL MÓVIL (Sincronizado con la tabla Usuario) ---
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            // 1. ASIGNACIÓN DE FEDERACIÓN (Tu lógica de seguridad)
            if (club.getFederacion() == null || club.getFederacion().getIdFederacion() == 0) {
                Federacion fedFija = fedDAO.read(1); 
                if (fedFija == null) {
                    fedFija = new Federacion();
                    fedFija.setIdFederacion(1);
                    // Si tu DAO permite crear federaciones de emergencia:
                    // fedDAO.create(fedFija); 
                }
                club.setFederacion(fedFija);
            }

            // 2. GUARDAR EN TABLA 'CLUB'
            // Esto guarda los datos específicos: CIF, dirección, etc.
            crearClub(club); 

            // 3. GUARDAR EN TABLA 'USUARIO' (La llave para el Login)
            // Creamos el objeto usuario que el ServicioUsuario buscará al hacer login
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(club.getNombre());
            nuevoUsuario.setEmail(club.getCorreo()); // Usamos el correo del club como login
            nuevoUsuario.setPassword(club.getPassword()); // La contraseña que viene del móvil
            nuevoUsuario.setRol(RolUsuario.CLUB); // Importante: marcamos que es un CLUB

            // Persistimos el usuario en la base de datos
            usuarioDAO.create(nuevoUsuario);

            System.out.println("[BACKEND] Registro doble completado: Club y Usuario creados para " + club.getCorreo());

            return ResponseEntity.ok("Club registrado correctamente. Ya puedes iniciar sesión.");
            
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Error en el registro doble: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE CONSULTA PARA EL MÓVIL ---
    @GetMapping("/listar")
    public List<Club> listarParaMovil() {
        return listarClubes();
    }

    // --- LÓGICA DE PERSISTENCIA ---
    
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
        // Lógica de inscripción
        System.out.println("Equipo " + e.getNombre() + " inscrito en " + t.getNombre());
    }
}
