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

    // --- NUEVO: Añadimos este para que el Escritorio pueda Validar/Rechazar ---
    public void actualizarClub(Club club) {
        servicioClub.actualizarClub(club);
    }

    public Equipo darAltaEquipo(String nombre, Club club, String categoria) {
        Equipo nuevoEquipo = new Equipo(nombre, club, categoria);
        servicioEquipo.crearEquipo(nuevoEquipo);
        return nuevoEquipo;
    }

    public void inscribirEnTorneo(Equipo e, Torneo t) {
        if ("APROBADO".equals(t.getEstado()) || "PROXIMO".equals(t.getEstado())) {
            servicioClub.inscribirEquipo(e, t);
        } else {
            System.out.println("No se puede inscribir: El torneo debe estar aprobado.");
        }
    }

    // --- CORRECCIÓN DE MÉTODOS (Para que coincidan con ServicioClub) ---
    
    public List<Club> listarClubes() {
        // Cambiamos la llamada interna para que use el método que existe en ServicioClub
        return servicioClub.listarParaMovil(); 
    }

    public Club obtenerClubPorNombre(String nombre) {
        // Cambiamos la llamada interna para que use el método que existe en ServicioClub
        return servicioClub.buscarPorNombre(nombre);
    }
}