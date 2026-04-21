package dao;

import java.util.List;
import org.hibernate.Session;
import modelo.*;
import util.HibernateUtil;

public class ParticipaDAO extends DAO<Participa> {

    public ParticipaDAO() {
        super(Participa.class);
    }

    // 🔹 Equipos que participan en un torneo
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
}
