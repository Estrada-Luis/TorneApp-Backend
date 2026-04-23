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
        // Obtiene la lista global de torneos para el catálogo principal
        return servicioTorneo.listarParaMovil();
    }

    public List<FilaClasificacion> obtenerClasificacionTorneo(int idTorneo) {
        // Devuelve la tabla de puntos y goles calculada en el servidor
        return servicioTorneo.obtenerClasificacion(idTorneo);
    }

    public List<Torneo> obtenerTorneosInscritosPorClub(int idClub) {
        // Historial de torneos donde el club está participando (Gestión Activa)
        return servicioTorneo.listarTorneosInscritos(idClub);
    }

    public List<Torneo> obtenerTorneosOrganizadosPorClub(int idClub) {
        // Torneos creados por el propio club
        return servicioTorneo.listarTorneosOrganizados(idClub);
    }

    public void actualizarTorneo(Torneo torneo) {
        // Persiste cambios en el estado o datos del torneo
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
        
        // 🚀 LA CLAVE: Asegúrate de que el club no sea null y tenga ID
        if (club != null && club.getIdClub() > 0) {
            torneo.setClubOrganizador(club);
        } else {
            // Log de error para que sepas si el problema viene de Android
            System.out.println("ERROR: El club viene vacío desde el móvil");
        }

        torneo.setEstado("PENDIENTE");
        
        // Aquí es donde se hace el session.save(torneo)
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
     * --- GESTIÓN DE INSCRIPCIONES ---
     * Métodos clave para la comunicación con DetalleInscripcionActivity
     */

    public void inscribirEquipoEnTorneo(int idTorneo, int idEquipo) {
        // Ejecuta la lógica de inscripción
        servicioTorneo.inscribirEquipo(idTorneo, idEquipo);
    }

    public List<Equipo> listarEquiposInscritosEnTorneo(int idTorneo) {
        // Recupera los equipos que ya están en el torneo para mostrarlos en la lista blanca
        return servicioTorneo.listarEquiposPorTorneo(idTorneo);
    }
}