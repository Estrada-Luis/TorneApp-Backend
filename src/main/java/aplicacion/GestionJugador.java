package aplicacion;

import modelo.Equipo;
import modelo.Jugador;
import servicio.ServicioJugador;
import java.util.List;

public class GestionJugador {

    private ServicioJugador servicioJugador;

    public GestionJugador() {
        this.servicioJugador = new ServicioJugador();
    }

    /**
     * Se llama desde el botón "Registro de Jugadores" de la App
     */
    public void altaJugador(String nombre, int edad, Equipo equipo) {
        servicioJugador.registrarJugador(nombre, edad, equipo);
    }

    public void mostrarPlantilla(Equipo equipo) {
        List<Jugador> jugadores = servicioJugador.listarJugadoresPorEquipo(equipo.getIdEquipo());
        System.out.println("\n--- PLANTILLA: " + equipo.getNombre() + " ---");
        jugadores.forEach(j -> System.out.println(j.getNombre() + " (" + j.getEdad() + " años)"));
    }
    public void bajaJugador(int id) {
        // Aquí podrías añadir una confirmación o log extra
        servicioJugador.eliminarJugador(id);
    }
}