package aplicacion;

import modelo.Equipo;
import modelo.Torneo;
import servicio.ServicioParticipa;
import java.util.List;

public class GestionParticipa {

    private ServicioParticipa servicioParticipa;

    public GestionParticipa() {
        this.servicioParticipa = new ServicioParticipa();
    }

    /**
     * Realiza la inscripción de un equipo en un torneo.
     */
    public void inscribirEquipoEnTorneo(Equipo equipo, Torneo torneo) {
        servicioParticipa.inscribirEquipo(equipo, torneo);
    }

    /**
     * Obtiene la lista de equipos inscritos en un torneo específico.
     */
    public List<Equipo> obtenerEquiposDeTorneo(int idTorneo) {
        return servicioParticipa.obtenerEquiposPorTorneo(idTorneo);
    }

    /**
     * ✅ NUEVO: Asigna un equipo a un grupo específico dentro de un torneo.
     * Útil para la gestión desde la interfaz de escritorio o lógica de sorteos.
     */
    public void asignarGrupoAEquipo(int idEquipo, int idTorneo, String grupo) {
        servicioParticipa.asignarGrupo(idEquipo, idTorneo, grupo);
    }
}