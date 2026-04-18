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

    // --- MÉTODO PARA EL MÓVIL: REGISTRO DOBLE (CLUB + USUARIO) ---
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            // 1. EXTRAER DATOS LIMPIOS (Evita errores de memoria en la BBDD)
            // Guardamos el nombre, correo y pass en variables antes de hacer nada
            String nombreTxt = club.getNombre();
            String correoTxt = club.getCorreo();
            String passTxt = club.getPassword();

            // 2. PARCHE DE FEDERACIÓN (Evita error 500 si el ID es 0)
            if (club.getFederacion() == null || club.getFederacion().getIdFederacion() == 0) {
                Federacion fedFija = fedDAO.read(1); 
                if (fedFija == null) {
                    fedFija = new Federacion();
                    fedFija.setIdFederacion(1);
                    // Si tu sistema requiere que exista físicamente, podrías crearla aquí
                }
                club.setFederacion(fedFija);
            }

            // 3. GUARDAR EN TABLA 'CLUB'
            // Esto es lo que ya te funcionaba bien
            clubDAO.create(club); 

            // 4. GUARDAR EN TABLA 'USUARIO' (Fundamental para el Login)
            // Si no hacemos esto, el login dará siempre Error 401
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombreTxt); 
            nuevoUsuario.setEmail(correoTxt);
            nuevoUsuario.setPassword(passTxt);
            nuevoUsuario.setRol(RolUsuario.CLUB);

            usuarioDAO.create(nuevoUsuario);

            System.out.println("[BACKEND] Registro exitoso para: " + correoTxt);
            return ResponseEntity.ok("Club y Usuario creados correctamente. Ya puedes iniciar sesión.");
            
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Error en el registro: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE CONSULTA Y LÓGICA ORIGINAL ---
    
    @GetMapping("/listar")
    public List<Club> listarParaMovil() {
        return listarClubes();
    }

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
