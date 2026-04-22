package servicio;

import modelo.Usuario;
import modelo.Equipo;
import modelo.RolUsuario;
import java.util.List;
import dao.UsuarioDAO;
import dao.EquipoDAO; // Necesario para buscar el equipo por código

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class ServicioUsuario {

    private UsuarioDAO usuarioDAO;
    private EquipoDAO equipoDAO; // Añadido para el registro con código

    public ServicioUsuario() {
        this.usuarioDAO = new UsuarioDAO();
        this.equipoDAO = new EquipoDAO();
    }

    // --- MÉTODO PARA EL MÓVIL (LOGIN) ---
    @PostMapping("/login")
    public ResponseEntity<?> loginDesdeMovil(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        Usuario u = obtenerUsuarioPorEmail(email);

        if (u != null && u.getPassword().equals(password)) {
            // Importante: Al enviar el usuario, Jackson incluirá el objeto Equipo si lo tiene
            return ResponseEntity.ok(u); 
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    /**
     * ✅ ACTUALIZADO: Registro desde móvil.
     * Ahora soporta el registro de entrenadores mediante código.
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarDesdeMovil(@RequestBody Map<String, Object> datos) {
        try {
            Usuario u = new Usuario();
            u.setNombre((String) datos.get("nombre"));
            u.setApellidos((String) datos.get("apellidos"));
            u.setEmail((String) datos.get("email"));
            u.setPassword((String) datos.get("password"));
            u.setRol(RolUsuario.valueOf((String) datos.get("rol")));
            u.setValidado(false);

            // Lógica para Entrenador con Código
            if (u.getRol() == RolUsuario.ENTRENADOR && datos.containsKey("codigoVinculacion")) {
                String codigo = (String) datos.get("codigoVinculacion");
                Equipo equipoEncontrado = null;
                
                // Buscamos el equipo que tenga ese código
                for (Equipo e : equipoDAO.readAll()) {
                    if (codigo.equalsIgnoreCase(e.getCodigoVinculacion())) {
                        equipoEncontrado = e;
                        break;
                    }
                }

                if (equipoEncontrado != null) {
                    u.setEquipo(equipoEncontrado);
                } else {
                    return ResponseEntity.status(400).body("El código de equipo no es válido.");
                }
            }

            crearUsuario(u);
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error en el registro: " + e.getMessage());
        }
    }

    // --- TU LÓGICA ORIGINAL ---

    public void crearUsuario(Usuario usuario) {
        if (usuario != null) {
            usuarioDAO.create(usuario);
        }
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.readAll();
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.isEmpty()) return null;
        return usuarioDAO.findByEmail(email);
    }
}