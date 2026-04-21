package aplicacion;

import modelo.Usuario;
import modelo.RolUsuario;
import servicio.ServicioUsuario;

public class GestionUsuario {

    private ServicioUsuario servicioUsuario;

    public GestionUsuario() {
        this.servicioUsuario = new ServicioUsuario();
    }

    /**
     * Actualizado para incluir apellidos y el estado de validación.
     */
    public Usuario registrarUsuario(String nombre, String apellidos, String email, String password, RolUsuario rol) {
        Usuario nuevoUsuario = new Usuario(nombre, email, password, rol);
        nuevoUsuario.setApellidos(apellidos);
        nuevoUsuario.setValidado(false); // Por defecto no validado hasta que el admin quiera
        
        servicioUsuario.crearUsuario(nuevoUsuario);
        return nuevoUsuario;
    }

    /**
     * Valida el login comprobando email y password.
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = servicioUsuario.obtenerUsuarioPorEmail(email);
        
        if (usuario != null && usuario.getPassword().equals(password)) {
            System.out.println("[APLICACIÓN] Login exitoso para: " + email);
            return usuario;
        }
        
        System.out.println("[APLICACIÓN] Intento de login fallido para: " + email);
        return null;
    }

    /**
     * Nuevo: Permite al administrador validar/activar a un usuario.
     */
    public void validarUsuario(Usuario usuario) {
        if (usuario != null) {
            usuario.setValidado(true);
            // Aquí llamarías a un método de actualizar en el servicio si lo necesitas
            System.out.println("[APLICACIÓN] Usuario " + usuario.getNombre() + " validado.");
        }
    }
}