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
        // Los servicios se instancian para acceder a la lógica de DAO y API
        this.servicioTorneo = new ServicioTorneo();
        this.servicioClub = new ServicioClub();
    }

    /**
     * MÉTODOS DE CLUBES
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
     * MÉTODOS DE TORNEOS
     */
    public List<Torneo> listarTorneos() {
        return servicioTorneo.listarTorneos();
    }

    public void actualizarTorneo(Torneo torneo) {
        servicioTorneo.actualizarTorneo(torneo);
    }

    /**
     * Obtiene torneos donde un club específico es el organizador.
     */
    public List<Torneo> obtenerTorneosOrganizadosPorClub(int idClub) {
        return servicioTorneo.listarTorneosOrganizados(idClub);
    }

    /**
     * Obtiene torneos donde el club tiene equipos inscritos.
     */
    public List<Torneo> obtenerTorneosInscritosPorClub(int idClub) {
        return servicioTorneo.listarTorneosInscritos(idClub);
    }

    /**
     * Método para crear torneo desde un CLUB
     */
    public void crearTorneo(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, String pdfUrl, Club club) {
        Torneo torneo = new Torneo();
        torneo.setNombre(nombre);
        torneo.setLocalizacion(localizacion);
        torneo.setFechaInicio(fechaInicio);
        torneo.setFechaFin(fechaFin);
        torneo.setCategoria(categoria);
        torneo.setNormas(normas);
        torneo.setPdfUrl(pdfUrl);
        torneo.setClubOrganizador(club);
        torneo.setEstado("PENDIENTE");
        
        servicioTorneo.crearTorneo(torneo);
    }

    /**
     * Método para crear torneo desde una FEDERACIÓN
     */
    public void crearTorneo(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, String pdfUrl, Federacion federacion) {
        Torneo torneo = new Torneo();
        torneo.setNombre(nombre);
        torneo.setLocalizacion(localizacion);
        torneo.setFechaInicio(fechaInicio);
        torneo.setFechaFin(fechaFin);
        torneo.setCategoria(categoria);
        torneo.setNormas(normas);
        torneo.setPdfUrl(pdfUrl);
        torneo.setFederacionOrganizador(federacion);
        torneo.setEstado("PROXIMO"); 
        
        servicioTorneo.crearTorneo(torneo);
    }

    /**
     * Devuelve el número de equipos inscritos en un torneo.
     */
    public int contarEquiposInscritos(int idTorneo) {
        return servicioTorneo.contarInscritos(idTorneo); 
    }
}