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

    public void inscribirEquipoEnTorneo(Equipo equipo, Torneo torneo) {
        servicioParticipa.inscribirEquipo(equipo, torneo);
    }

    public List<Equipo> obtenerEquiposDeTorneo(int idTorneo) {
        return servicioParticipa.obtenerEquiposPorTorneo(idTorneo);
    }
}