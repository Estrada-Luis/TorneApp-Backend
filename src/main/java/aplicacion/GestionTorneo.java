package aplicacion;

import modelo.Club;
import modelo.Federacion;
import modelo.Torneo;
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
     * MÉTODOS DE TORNEOS (Cuadrados con ServicioTorneo)
     */
    
    public List<Torneo> listarTorneos() {
        // Llama a listarParaMovil() de tu servicio
        return servicioTorneo.listarParaMovil();
    }

    public List<FilaClasificacion> obtenerClasificacionTorneo(int idTorneo) {
        // Llama a obtenerClasificacion(int) de tu servicio
        return servicioTorneo.obtenerClasificacion(idTorneo);
    }

    public List<Torneo> obtenerTorneosInscritosPorClub(int idClub) {
        // Llama a listarTorneosInscritos(int) de tu servicio
        return servicioTorneo.listarTorneosInscritos(idClub);
    }

    public List<Torneo> obtenerTorneosOrganizadosPorClub(int idClub) {
        // Llama a listarTorneosOrganizados(int) de tu servicio
        return servicioTorneo.listarTorneosOrganizados(idClub);
    }

    /**
     * MÉTODOS DE CREACIÓN 
     * Nota: En tu servicio solo existe crearTorneoConArchivo.
     * He dejado estos métodos listos para que rellenes el objeto Torneo.
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
        
        // Aquí podrías llamar a un método del DAO o añadir crearTorneo(Torneo) a tu servicio
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
    }
 // AÑADE ESTO AL FINAL DE GestionTorneo.java
    public void actualizarTorneo(Torneo torneo) {
        // Llama al servicio de torneos para persistir los cambios
        servicioTorneo.actualizarTorneo(torneo);
    }
}