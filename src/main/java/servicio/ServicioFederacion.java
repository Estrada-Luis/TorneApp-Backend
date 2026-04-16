package servicio;

import dao.DAO;
import modelo.Federacion;
import modelo.Club;
import modelo.Torneo;
import java.util.List;

public class ServicioFederacion {
    private DAO<Federacion> federacionDAO = new DAO<>(Federacion.class);
    private DAO<Club> clubDAO = new DAO<>(Club.class);
    private DAO<Torneo> torneoDAO = new DAO<>(Torneo.class);

    public void crearFederacion(Federacion f) { federacionDAO.create(f); }

    public void cambiarEstadoValidacionClub(int id, boolean estado) {
        Club c = clubDAO.read(id);
        if (c != null) {
            c.setValidado(estado);
            clubDAO.update(c);
        }
    }

    public void cambiarEstadoTorneo(int id, String estado) {
        Torneo t = torneoDAO.read(id);
        if (t != null) {
            t.setEstado(estado);
            torneoDAO.update(t);
        }
    }

    public List<Federacion> listarFederaciones() { return federacionDAO.readAll(); }
}
