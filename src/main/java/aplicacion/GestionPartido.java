package aplicacion;

import modelo.Partido;
import modelo.Torneo;
import servicio.ServicioPartido;
import java.util.List;

public class GestionPartido {

    private ServicioPartido servicioPartido;

    public GestionPartido() {
        // Inicializamos el servicio que conecta con el DAO y la API
        this.servicioPartido = new ServicioPartido();
    }

    /**
     * Crea un partido con toda la información necesaria, incluyendo la fase.
     */
    public void crearPartido(String fecha, String hora, String resultado, String tipoFase, String nombreFase, Torneo torneo) {
        // Creamos el objeto
        Partido partido = new Partido();
        
        // Asignamos los datos básicos
        partido.setFecha(fecha);
        partido.setHora(hora);
        partido.setResultado(resultado);
        partido.setTorneo(torneo);
        
        // Asignamos la información de la fase (Grupos/Eliminatoria)
        partido.setTipoFase(tipoFase);     // Ejemplo: "GRUPOS"
        partido.setNombreFase(nombreFase); // Ejemplo: "Grupo B - Jornada 2"
        
        // Guardamos a través del servicio
        servicioPartido.crearPartido(partido);
    }

    /**
     * Obtiene el calendario completo de un torneo específico.
     */
    public List<Partido> listarPartidosPorTorneo(int idTorneo) {
        return servicioPartido.listarPartidosPorTorneo(idTorneo);
    }

    /**
     * Busca un partido concreto por su ID.
     */
    public Partido obtenerPartido(int idPartido) {
        return servicioPartido.obtenerPartido(idPartido);
    }

    /**
     * Actualiza el resultado de un partido (Útil para el panel de administración).
     */
    public void actualizarResultado(int idPartido, String nuevoResultado) {
        Partido p = servicioPartido.obtenerPartido(idPartido);
        if (p != null) {
            p.setResultado(nuevoResultado);
            servicioPartido.actualizarPartido(p);
        }
    }
    
    // Si necesitas eliminar o listar todos, puedes añadir los puentes aquí
}