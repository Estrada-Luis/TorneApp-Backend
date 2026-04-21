package dao;

import modelo.Usuario;
import java.util.List;

public class UsuarioDAO extends DAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    /**
     * Ahora este método es mucho más limpio porque aprovecha la lógica
     * de transacciones seguras que escribimos en la clase padre DAO.
     */
    public Usuario findByEmail(String email) {
        // Usamos el método que añadimos al DAO genérico
        List<Usuario> resultados = super.readByProperty("email", email);
        
        if (resultados != null && !resultados.isEmpty()) {
            return resultados.get(0);
        }
        return null;
    }
    
    // Los métodos create y read YA NO HACEN FALTA aquí 
    // porque ya están en DAO<T> y funcionan para cualquier clase.
}
