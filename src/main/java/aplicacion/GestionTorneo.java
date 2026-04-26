package aplicacion;

import modelo.*;
import servicio.ServicioClub;
import servicio.ServicioTorneo;
import servicio.ServicioTorneo.FilaClasificacion; // Importante para la tabla
import java.util.List;
import dao.ParticipaDAO;

public class GestionTorneo {

    private ServicioTorneo servicioTorneo;
    private ServicioClub servicioClub;

    public GestionTorneo() {
        this.servicioTorneo = new ServicioTorneo();
        this.servicioClub = new ServicioClub();
    }

    /**
     * --- 🏢 MÉTODOS DE CLUBES ---
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
     * --- 🏆 MÉTODOS DE CREACIÓN DE TORNEOS ---
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

    public void cerrarActa(int idPartido) {
        servicioTorneo.cerrarActa(idPartido);
    }

    public void generarCalendarioGrupos(int idTorneo) {
        servicioTorneo.generarCalendarioGrupos(idTorneo);
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

    // 🚩 CORREGIDO: Nombre de método para que coincida con el Controller
    public List<FilaClasificacion> obtenerClasificacion(int idTorneo) {
        return servicioTorneo.obtenerClasificacion(idTorneo);
    }

    public void aprobarTorneo(int idTorneo) {
        servicioTorneo.aprobarTorneo(idTorneo);
    }

    public List<Torneo> buscarTorneosPorEstado(String estado) {
        return servicioTorneo.buscarPorEstado(estado);
    }

    public String obtenerGrupoEquipo(int idEquipo, int idTorneo) {
        try {
            return ParticipaDAO.obtenerGrupo(idEquipo, idTorneo); 
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    /**
     * ✅ MÉTODOS PARA EL MÓVIL
     */
    public void inscribirEquipoEnTorneo(int idTorneo, int idEquipo) {
        servicioTorneo.inscribirEquipo(idTorneo, idEquipo);
    }

    public List<Torneo> listarTorneosInscritosPorClub(int idClub) {
        return servicioTorneo.listarTorneosInscritosPorClub(idClub);
    }

    public List<Torneo> listarTorneosOrganizadosPorClub(int idClub) {
        return servicioTorneo.listarTorneosOrganizadosPorClub(idClub);
    }
}