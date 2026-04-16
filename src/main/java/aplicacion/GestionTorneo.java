package aplicacion;

import modelo.Club;
import modelo.Federacion;
import modelo.Torneo;
import servicio.ServicioClub;
import servicio.ServicioTorneo;
import java.util.List;

public class GestionTorneo {

    private ServicioTorneo servicioTorneo;
    private ServicioClub servicioClub;

    public GestionTorneo() {
        this.servicioTorneo = new ServicioTorneo();
        this.servicioClub = new ServicioClub();
    }

    /**
     * MÉTODOS DE CLUBES
     */
    public List<Club> listarClubs() {
        return servicioClub.listarClubes();
    }

    // ✅ NUEVO: Para que el Admin pueda guardar los cambios al aprobar un club
    public void actualizarClub(Club club) {
        servicioClub.actualizarClub(club);
    }

    // ✅ NUEVO: Para que el Admin pueda borrar el club si rechaza la solicitud
    public void eliminarClub(Club club) {
        servicioClub.eliminarClub(club);
    }

    /**
     * MÉTODOS DE TORNEOS
     */
    public List<Torneo> listarTorneos() {
        return servicioTorneo.listarTorneos();
    }

    public void actualizarTorneo(Torneo torneo) {
        servicioTorneo.actualizarTorneo(torneo);
    }

    public void crearTorneo(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, Club club) {
        Torneo torneo = new Torneo();
        torneo.setNombre(nombre);
        torneo.setLocalizacion(localizacion);
        torneo.setFechaInicio(fechaInicio);
        torneo.setFechaFin(fechaFin);
        torneo.setCategoria(categoria);
        torneo.setNormas(normas);
        torneo.setClubOrganizador(club);
        torneo.setEstado("PENDIENTE");
        
        servicioTorneo.crearTorneo(torneo);
    }

    public void crearTorneo(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, Federacion federacion) {
        Torneo torneo = new Torneo();
        torneo.setNombre(nombre);
        torneo.setLocalizacion(localizacion);
        torneo.setFechaInicio(fechaInicio);
        torneo.setFechaFin(fechaFin);
        torneo.setCategoria(categoria);
        torneo.setNormas(normas);
        torneo.setFederacionOrganizador(federacion);
        torneo.setEstado("PROXIMO"); 
        
        servicioTorneo.crearTorneo(torneo);
    }

    public int contarEquiposInscritos(int idTorneo) {
        return servicioTorneo.contarInscritos(idTorneo); 
    }
}