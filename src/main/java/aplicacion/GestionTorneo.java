package aplicacion;

import modelo.*;
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
     * --- 🏢 MÉTODOS DE CLUBES (RESTAURADOS) ---
     * Esto arregla el error en ValidarClubController
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
     * --- 🏆 MÉTODOS DE CREACIÓN DE TORNEOS (RESTAURADOS) ---
     * Esto arregla el error en TorneoController
     */
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

    public void crearTorneoDesdeClub(String nombre, String localizacion, String fechaInicio, String fechaFin, String categoria, String normas, Club club) {
        Torneo torneo = new Torneo();
        torneo.setNombre(nombre);
        torneo.setLocalizacion(localizacion);
        torneo.setFechaInicio(fechaInicio);
        torneo.setFechaFin(fechaFin);
        torneo.setCategoria(categoria);
        torneo.setNormas(normas);
        
        if (club != null) {
            torneo.setClubOrganizador(club);
        }

        torneo.setEstado("PENDIENTE");
        servicioTorneo.crearTorneo(torneo);
    }

    /**
     * --- ⚽ GESTIÓN DE PARTIDOS Y ACTAS ---
     */
    public List<Partido> listarPartidosPorTorneo(int idTorneo) {
        return servicioTorneo.listarPartidosPorTorneo(idTorneo);
    }

    public void actualizarResultado(int idPartido, int golesLocal, int golesVisitante) {
        servicioTorneo.actualizarResultado(idPartido, golesLocal, golesVisitante);
    }

    public void generarFaseFinal(int idTorneo) {
        servicioTorneo.generarFaseFinal(idTorneo);
    }

    /**
     * --- 📋 CONSULTAS GENERALES DE TORNEOS ---
     */
    public List<Torneo> listarTorneos() {
        return servicioTorneo.listarParaMovil();
    }

    public void actualizarTorneo(Torneo torneo) {
        servicioTorneo.actualizarTorneo(torneo);
    }

    public List<Equipo> listarEquiposPorTorneo(int idTorneo) {
        return servicioTorneo.listarEquiposPorTorneo(idTorneo);
    }

    public void asignarGrupo(int idEquipo, int idTorneo, String grupo) {
        servicioTorneo.asignarGrupo(idEquipo, idTorneo, grupo);
    }

    public List<FilaClasificacion> obtenerClasificacionTorneo(int idTorneo) {
        return servicioTorneo.obtenerClasificacion(idTorneo);
    }
}