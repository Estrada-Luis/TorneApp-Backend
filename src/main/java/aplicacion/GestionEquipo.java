package aplicacion;

import modelo.Club;
import modelo.Equipo;
import modelo.Usuario;
import servicio.ServicioEquipo;
import java.util.List;

public class GestionEquipo {

    private ServicioEquipo servicioEquipo;

    public GestionEquipo() {
        this.servicioEquipo = new ServicioEquipo();
    }

    public void crearEquipo(Equipo equipo) {
        if (equipo != null) {
            servicioEquipo.crearEquipo(equipo);
        } else {
            System.out.println("[ERROR] El equipo proporcionado es nulo.");
        }
    }

    public List<Equipo> listarEquipos() {
        return servicioEquipo.listarEquipos();
    }
    
    public List<Equipo> listarEquiposPorClub(int idClub) {
        return servicioEquipo.listarEquiposPorClub(idClub);
    }

    /**
     * ✅ NUEVO: Permite que la versión de escritorio también consulte 
     * la plantilla de jugadores si fuera necesario.
     */
    public List<Usuario> listarJugadoresDeEquipo(int idEquipo) {
        return servicioEquipo.listarJugadoresDeEquipo(idEquipo);
    }
    
    /**
     * ✅ NUEVO: Para actualizar cambios en el equipo (como la división)
     */
    public void actualizarEquipo(Equipo equipo) {
        servicioEquipo.actualizarEquipo(equipo);
    }
}