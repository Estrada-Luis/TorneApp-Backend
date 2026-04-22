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
     */
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Club club) {
        try {
            String nombreTxt = club.getNombre();
            String correoTxt = club.getCorreo(); 
            String passTxt = club.getPassword();

            if (correoTxt == null || passTxt == null) {
                return ResponseEntity.status(400).body("Error: Datos incompletos (email o password)");
            }

            if (club.getFederacion() == null || club.getFederacion().getIdFederacion() == 0) {
                Federacion fedFija = fedDAO.read(1); 
                if (fedFija == null) {
                    fedFija = new Federacion();
                    fedFija.setIdFederacion(1);
                }
                club.setFederacion(fedFija);
            }

            clubDAO.create(club); 

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombreTxt); 
            nuevoUsuario.setEmail(correoTxt); 
            nuevoUsuario.setPassword(passTxt);
            nuevoUsuario.setRol(RolUsuario.CLUB);
            nuevoUsuario.setValidado(false); // Sincronizado con el estado inicial

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

    /**
     * MÉTODO CLAVE: El móvil llama aquí para ver si el club ha sido rechazado
     * Ruta: GET /api/clubes/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Club> obtenerPorEmail(@PathVariable String email) {
        try {
            List<Club> lista = clubDAO.readAll();
            if (lista == null) return ResponseEntity.notFound().build();
            
            // Buscamos el club que coincida con el correo enviado
            Club clubEncontrado = lista.stream()
                    .filter(c -> c.getCorreo() != null && c.getCorreo().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);

            if (clubEncontrado != null) {
                return ResponseEntity.ok(clubEncontrado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody Club clubMovil) {
        try {
            // 1. Buscamos el club real en la DB
            Club clubDB = clubDAO.read(clubMovil.getIdClub());
            
            if (clubDB == null) {
                return ResponseEntity.status(404).body("Error: Club no encontrado");
            }

            // 2. Actualizamos los campos de texto
            clubDB.setNombre(clubMovil.getNombre());
            clubDB.setDireccion(clubMovil.getDireccion());
            clubDB.setTelefono(clubMovil.getTelefono());
            
            // --- 🛡️ CAMBIO CLAVE AQUÍ ---
            // En lugar de aceptar lo que venga del móvil, reseteamos el estado.
            // Al poner validado en FALSE y motivo en NULL, el cartel rojo desaparece
            // y vuelve a salir el amarillo de "Pendiente" en la App.
            clubDB.setValidado(false); 
            clubDB.setMotivoRechazo(null); 

            // 4. Actualizamos la federación si viene
            if (clubMovil.getFederacion() != null && clubMovil.getFederacion().getIdFederacion() != 0) {
                clubDB.setFederacion(clubMovil.getFederacion());
            }

            // Guardamos en la base de datos
            clubDAO.update(clubDB);
            
            System.out.println("[BACKEND] Perfil actualizado y reseteado a pendiente para: " + clubDB.getCorreo());
            return ResponseEntity.ok("Club actualizado correctamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al actualizar: " + e.getMessage());
        }
    }
    // --- MÉTODOS COMPLEMENTARIOS (Lógica para el Escritorio) ---

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

    public void inscribirEquipo(Equipo e, Torneo t) {
        System.out.println("Inscribiendo equipo: " + e.getNombre() + " en torneo: " + t.getNombre());
    }
}