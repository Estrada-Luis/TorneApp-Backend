package servicio;

import dao.JugadorDAO;
import modelo.Jugador;
import modelo.Equipo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioJugador {

    private JugadorDAO jugadorDAO;

    public ServicioJugador() {
        this.jugadorDAO = new JugadorDAO();
    }

    public void registrarJugador(String nombre, int edad, Equipo equipo) {
        Jugador nuevoJugador = new Jugador(nombre, edad, equipo);
        jugadorDAO.create(nuevoJugador);
    }

    public List<Jugador> listarJugadoresPorEquipo(int idEquipo) {
        // 1. Creamos una lista vacía para almacenar los jugadores encontrados
        List<Jugador> jugadoresFiltrados = new ArrayList<>();

        // 2. Recorremos todos los jugadores de la base de datos
        for (Jugador j : jugadorDAO.readAll()) {
            // 3. Comprobamos si el ID del equipo del jugador coincide con el buscado
            if (j.getEquipo() != null && j.getEquipo().getIdEquipo() == idEquipo) {
                jugadoresFiltrados.add(j);
            }
        }

        // 4. Devolvemos la lista con los resultados
        return jugadoresFiltrados;
    }
    
    public void eliminarJugador(int id) {
        // Primero recuperamos el objeto de la BD
        Jugador j = jugadorDAO.read(id);
        if (j != null) {
            jugadorDAO.delete(j);
            System.out.println("[SERVICIO] Jugador con ID " + id + " eliminado.");
        } else {
            System.out.println("[SERVICIO] Error: No se encontró el jugador.");
        }
    }
}