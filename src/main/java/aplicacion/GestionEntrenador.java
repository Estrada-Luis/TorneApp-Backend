package aplicacion;

import modelo.Usuario;
import modelo.Entrenador;
import modelo.Torneo;
import modelo.Equipo;
import servicio.ServicioEntrenador;

public class GestionEntrenador {

    private ServicioEntrenador servicioEntrenador;

    public GestionEntrenador() {
        this.servicioEntrenador = new ServicioEntrenador();
    }

    public Entrenador registrarEntrenadorConCodigo(Usuario usuario, String codigo, String telefono) {
        return servicioEntrenador.registrarEntrenadorConCodigo(usuario, codigo, telefono);
    }

    // FUNCIÓN DE COMUNICACIÓN INTERNA: Sugerir torneo al coordinador
    public void sugerirTorneoAlClub(Torneo torneo, Equipo equipo, String mensaje) {
        // Aquí se podría implementar una notificación o un registro de sugerencia
        System.out.println("[ENTRENADOR] " + equipo.getNombre() + " sugiere participar en: " + torneo.getNombre());
        System.out.println("Mensaje para el coordinador: " + mensaje);
    }
    
    public void eliminarEntrenador(int id) {
        servicioEntrenador.eliminarEntrenador(id);
    }
}