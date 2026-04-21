package aplicacion;

import modelo.Club;
import modelo.Equipo;
import modelo.Torneo;
import servicio.ServicioEquipo;
import servicio.ServicioClub;
import java.util.List;

public class GestionClub {
    private ServicioEquipo servicioEquipo;
    private ServicioClub servicioClub;

    public GestionClub() {
        this.servicioEquipo = new ServicioEquipo();
        this.servicioClub = new ServicioClub();
    }

    public void crearClub(Club club) {
        servicioClub.crearClub(club);
    }

    /**
     * Este es el método que usa tu aplicación de escritorio.
     * Al llamar a servicioClub.actualizarClub(club), ahora se beneficiará
     * del 'updatable = false' que pusimos en la entidad Club para el CIF.
     */
    public void actualizarClub(Club club) {
        servicioClub.actualizarClub(club);
    }

    public Equipo darAltaEquipo(String nombre, Club club, String categoria) {
        Equipo nuevoEquipo = new Equipo(nombre, club, categoria);
        servicioEquipo.crearEquipo(nuevoEquipo);
        return nuevoEquipo;
    }

    public void inscribirEnTorneo(Equipo e, Torneo t) {
        // Mantenemos tu lógica de estados tal cual
        if ("APROBADO".equals(t.getEstado()) || "PROXIMO".equals(t.getEstado())) {
            servicioClub.inscribirEquipo(e, t);
        } else {
            System.out.println("No se puede inscribir: El torneo debe estar aprobado.");
        }
    }

    public List<Club> listarClubes() {
        return servicioClub.listarParaMovil(); 
    }

    public Club obtenerClubPorNombre(String nombre) {
        return servicioClub.buscarPorNombre(nombre);
    }
}