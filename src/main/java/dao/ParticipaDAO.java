package dao;

import java.util.List;
import org.hibernate.Session;
import modelo.*;
import util.HibernateUtil;

public class ParticipaDAO extends DAO<Participa> {

    public ParticipaDAO() {
        super(Participa.class);
    }

    /**
     * Busca una inscripción específica usando la clave compuesta.
     * Es vital para actualizar el "grupo" de un equipo.
     */
    public Participa read(ParticipaId id) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        Participa participa = session.get(Participa.class, id);
        session.getTransaction().commit();
        return participa;
    }

    /**
     * Obtiene solo los objetos Equipo de un torneo.
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();

        List<Equipo> equipos = session.createQuery(
                "select p.equipo from Participa p where p.torneo.idTorneo = :id",
                Equipo.class
        ).setParameter("id", idTorneo)
         .getResultList();

        session.getTransaction().commit();
        return equipos;
    }

    /**
     * Obtiene los objetos Participa completos (incluyendo el campo grupo).
     * Útil para la pestaña de "Inscritos" en Android.
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();

        List<Participa> lista = session.createQuery(
                "from Participa p where p.torneo.idTorneo = :id",
                Participa.class
        ).setParameter("id", idTorneo)
         .getResultList();

        session.getTransaction().commit();
        return lista;
    }
}
