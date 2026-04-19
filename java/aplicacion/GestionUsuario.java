package aplicacion;

import modelo.Usuario;
import modelo.RolUsuario;
import servicio.ServicioUsuario;

public class GestionUsuario {

    private ServicioUsuario servicioUsuario;

    public GestionUsuario() {
        this.servicioUsuario = new ServicioUsuario();
    }

    public Usuario registrarUsuario(String nombre, String email, String password, RolUsuario rol) {
        Usuario nuevoUsuario = new Usuario(nombre, email, password, rol);
        servicioUsuario.crearUsuario(nuevoUsuario);
        return nuevoUsuario;
    }

    // --- NUEVO MÉTODO PARA EL LOGIN ---
    public Usuario validarLogin(String email, String password) {
        // Buscamos al usuario en la base de datos a través del servicio
        Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(email);
        
        // Verificamos si existe y si la contraseña coincide
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario; // Login correcto
        }
        return null; // Login fallido
    }
}