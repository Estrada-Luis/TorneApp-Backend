package aplicacion;

import modelo.Club;
import modelo.Federacion;
import modelo.Torneo;
import modelo.Equipo;
import servicio.ServicioClub;
import servicio.ServicioTorneo;
import servicio.ServicioTorneo.FilaClasificacion; 
import java.util.List;

public class GestionTorneo {

    private ServicioTorneo servicioTorneo;
    private ServicioClub servicioClub;

    public GestionTorneo() {
        this.servicioTorneo = new ServicioTorneo();
        this.servicioClub = new ServicioClub();
    }

    /**
     * --- MÉTODOS DE CLUBES ---
     */
    public List<Club> listarClubs() {
        return servicioClub.listarParaMovil(); 
    }

    public void actualizarClub(Club club) {
        servicioClub.actualizarClub(club);
    }

    public void eliminarClub(Club club) {
        servicioClub.eliminarClub(club);
    }

    /**
     * --- MÉTODOS DE TORNEOS ---
     */
    
    public List<Torneo> listarTorneos() {
        return servicioTorneo.listarParaMovil();
    }

    public List<FilaClasificacion> obtenerClasificacionTorneo(int idTorneo) {
        return servicioTorneo.obtenerClasificacion(idTorneo);
    }

    public List<Torneo> obtenerTorneosInscritosPorClub(int idClub) {
        // Llama al servicio que acabamos de completar
        return servicioTorneo.listarTorneosInscritos(idClub);
    }

    public List<Torneo> obtenerTorneosOrganizadosPorClub(int idClub) {
        return servicioTorneo.listarTorneosOrganizados(idClub);
    }

    public void actualizarTorneo(Torneo torneo) {
        servicioTorneo.actualizarTorneo(torneo);
    }

    /**
     * --- MÉTODOS DE CREACIÓN ---
     */

    public void crearTorneoDesdeClub(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, Club club) {
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

    public void crearTorneoDesdeFederacion(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, Federacion federacion) {
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

    /**
     * Método puente para inscribir equipos desde la lógica de aplicación
     */
    public void inscribirEquipoEnTorneo(int idTorneo, int idEquipo) {
        servicioTorneo.inscribirEquipo(idTorneo, idEquipo);
    }
}