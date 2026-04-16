package aplicacion;

import modelo.Club;
import modelo.Equipo;
import servicio.ServicioEquipo;
import java.util.List;

public class GestionEquipo {

    private ServicioEquipo servicioEquipo;

    public GestionEquipo() {
        this.servicioEquipo = new ServicioEquipo();
    }

    /**
     * Cambiamos el método para que acepte el objeto Equipo completo,
     * que es lo que estás intentando hacer en el Main.
     */
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
    
    // Si necesitas buscar por Club, este método también es útil
    public List<Equipo> listarEquiposPorClub(int idClub) {
        return servicioEquipo.listarEquiposPorClub(idClub);
    }
}