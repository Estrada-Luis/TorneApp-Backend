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
        // Importante: Asegúrate de que los constructores de los servicios sean accesibles
        this.servicioTorneo = new ServicioTorneo();
        this.servicioClub = new ServicioClub();
    }

    /**
     * MÉTODOS DE CLUBES
     */
    public List<Club> listarClubs() {
        // Coincide con el método de ServicioClub que devuelve la lista
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
     * Método para crear torneo desde un CLUB
     * Se han mapeado todos los nombres: nombre, localizacion, fechaInicio, fechaFin, categoria, normas y pdfUrl.
     */
    public void crearTorneo(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, String pdfUrl, Club club) {
        Torneo torneo = new Torneo();
        torneo.setNombre(nombre);
        torneo.setLocalizacion(localizacion);
        torneo.setFechaInicio(fechaInicio);
        torneo.setFechaFin(fechaFin);
        torneo.setCategoria(categoria);
        torneo.setNormas(normas);
        torneo.setPdfUrl(pdfUrl); // Coincide con el setter de la entidad Torneo
        torneo.setClubOrganizador(club);
        torneo.setEstado("PENDIENTE");
        
        // Llama al método crearTorneo(Torneo t) de ServicioTorneo
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
        torneo.setPdfUrl(pdfUrl); // Coincide con el setter de la entidad Torneo
        torneo.setFederacionOrganizador(federacion);
        torneo.setEstado("PROXIMO"); 
        
        // Llama al método crearTorneo(Torneo t) de ServicioTorneo
        servicioTorneo.crearTorneo(torneo);
    }

    public int contarEquiposInscritos(int idTorneo) {
        // Asegúrate de que ServicioTorneo tenga este método implementado
        return servicioTorneo.contarInscritos(idTorneo); 
    }
}