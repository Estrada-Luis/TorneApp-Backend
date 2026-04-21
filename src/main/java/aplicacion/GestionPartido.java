package aplicacion;

import modelo.Partido;
import modelo.Torneo;
import servicio.ServicioPartido;
import java.util.List;

public class GestionPartido {

    private ServicioPartido servicioPartido;

    public GestionPartido() {
        this.servicioPartido = new ServicioPartido();
    }

    public void crearPartido(String fecha, String hora, String resultado, Torneo torneo) {
        // Creamos el objeto vacío
        Partido partido = new Partido();
        
        // Asignamos los datos (Ahora los métodos existen)
        partido.setFecha(fecha);
        partido.setHora(hora);
        partido.setResultado(resultado);
        partido.setTorneo(torneo);
        
        // Guardamos
        servicioPartido.crearPartido(partido);
    }

    public List<Partido> listarPartidosPorTorneo(int idTorneo) {
        return servicioPartido.listarPartidosPorTorneo(idTorneo);
    }

    public Partido obtenerPartido(int idPartido) {
        return servicioPartido.obtenerPartido(idPartido);
    }
}