package aplicacion;

import modelo.Federacion;
import servicio.ServicioFederacion;
import java.util.List;

public class GestionFederacion {
    private ServicioFederacion servicioFederacion;

    public GestionFederacion() {
        this.servicioFederacion = new ServicioFederacion();
    }

    public void crearFederacion(String nombre, String direccion, String correo, String telefono) {
        Federacion f = new Federacion(nombre, direccion, correo, telefono);
        servicioFederacion.crearFederacion(f);
    }

    public void validarClub(int idClub, boolean estado) {
        servicioFederacion.cambiarEstadoValidacionClub(idClub, estado);
    }

    public void validarTorneo(int idTorneo, String estado) {
        servicioFederacion.cambiarEstadoTorneo(idTorneo, estado);
    }

    public List<Federacion> listarFederaciones() {
        return servicioFederacion.listarFederaciones();
    }
}