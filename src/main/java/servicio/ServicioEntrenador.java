package servicio;

import dao.DAO;
import modelo.Entrenador;
import modelo.Equipo;
import modelo.Usuario;

// Importaciones para la conexión móvil
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/entrenadores")
public class ServicioEntrenador {

    private DAO<Entrenador> entrenadorDAO;
    private DAO<Equipo> equipoDAO;

    public ServicioEntrenador() {
        this.entrenadorDAO = new DAO<>(Entrenador.class);
        this.equipoDAO = new DAO<>(Equipo.class);
    }

    // --- MÉTODO PARA EL MÓVIL ---
    // Recibe un objeto con el usuario, el código de equipo y el teléfono
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarDesdeMovil(@RequestBody Map<String, Object> datos) {
        try {
            // Extraemos los datos que envía el móvil
            // Nota: Esto asume que el móvil envía un JSON con estas llaves
            Usuario usuario = (Usuario) datos.get("usuario"); 
            String codigo = (String) datos.get("codigo");
            String telefono = (String) datos.get("telefono");

            Entrenador resultado = registrarEntrenadorConCodigo(usuario, codigo, telefono);

            if (resultado != null) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(404).body("Código de equipo no válido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // --- TU LÓGICA ORIGINAL (No se toca) ---

    public Entrenador registrarEntrenadorConCodigo(Usuario usuario, String codigo, String telefono) {
        // 1. Buscamos el equipo recorriendo la lista manualmente
        Equipo equipoEncontrado = null;
        
        for (Equipo e : equipoDAO.readAll()) {
            if (e.getCodigoVinculacion() != null && e.getCodigoVinculacion().equals(codigo)) {
                equipoEncontrado = e;
                break; 
            }
        }

        // 2. Si no lo encontramos, salimos
        if (equipoEncontrado == null) {
            return null;
        }

        // 3. Si lo encontramos, creamos y guardamos al entrenador
        Entrenador nuevoEntrenador = new Entrenador(telefono, equipoEncontrado, usuario);
        entrenadorDAO.create(nuevoEntrenador);
        
        return nuevoEntrenador;
    }
    
    public void eliminarEntrenador(int id) {
        Entrenador e = entrenadorDAO.read(id);
        if (e != null) {
            entrenadorDAO.delete(e);
        }
    }
}