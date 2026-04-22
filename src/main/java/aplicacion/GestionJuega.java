package aplicacion;

import modelo.Equipo;
import modelo.Partido;
import servicio.ServicioJuega;
import java.util.List;

public class GestionJuega {

    private ServicioJuega servicioJuega;

    public GestionJuega() {
        this.servicioJuega = new ServicioJuega();
    }

    /**
     * Vincula un equipo a un partido específico.
     */
    public void asignarEquipoAPartido(Equipo equipo, Partido partido) {
        servicioJuega.añadirEquipoAPartido(equipo, partido);
    }

    /**
     * Obtiene la lista de equipos participantes en un encuentro.
     */
    public List<Equipo> obtenerEquiposDePartido(int idPartido) {
        return servicioJuega.obtenerEquiposPorPartido(idPartido);
    }

    /**
     * Registra el marcador final y reparte los puntos de clasificación.
     * Este método es el que usarás si decides meter resultados desde la App de escritorio.
     */
    public void registrarResultadoFinal(int idPartido, int golesLocal, int golesVisitante) {
        servicioJuega.actualizarResultado(idPartido, golesLocal, golesVisitante);
    }
}