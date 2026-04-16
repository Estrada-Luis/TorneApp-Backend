package servicio;

import modelo.Usuario;
import java.util.List;
import dao.UsuarioDAO;

// Importaciones para el Login desde Android
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class ServicioUsuario {

    private UsuarioDAO usuarioDAO;

    public ServicioUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // --- MÉTODO PARA EL MÓVIL (LOGIN) ---
    @PostMapping("/login")
    public ResponseEntity<?> loginDesdeMovil(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        Usuario u = obtenerUsuarioPorEmail(email);

        if (u != null && u.getPassword().equals(password)) {
            return ResponseEntity.ok(u); // Login correcto, mandamos el usuario al móvil
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    // --- MÉTODO PARA EL MÓVIL (REGISTRO) ---
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarDesdeMovil(@RequestBody Usuario usuario) {
        try {
            crearUsuario(usuario);
            return ResponseEntity.ok("Usuario registrado");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // --- TU LÓGICA ORIGINAL ---

    public void crearUsuario(Usuario usuario) {
        if (usuario != null) {
            usuarioDAO.create(usuario);
            System.out.println("[SERVICIO] Usuario guardado: " + usuario.getEmail());
        }
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.readAll();
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return usuarioDAO.findByEmail(email);
    }
}