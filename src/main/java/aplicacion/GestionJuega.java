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

    public void asignarEquipoAPartido(Equipo equipo, Partido partido) {
        servicioJuega.añadirEquipoAPartido(equipo, partido);
    }

    public List<Equipo> obtenerEquiposDePartido(int idPartido) {
        return servicioJuega.obtenerEquiposPorPartido(idPartido);
    }
}