package dao;

import modelo.Club;
import util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class ClubDAO extends DAO<Club> {

    public ClubDAO() {
        super(Club.class);
    }


    public void create(Club club) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        session.save(club);
        session.getTransaction().commit();
    }

    public Club read(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        Club club = session.get(Club.class, idClub);
        session.getTransaction().commit();
        return club;
    }
    public long countPendientes() {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        
        // Consulta HQL: Cuenta los clubes donde validado es false
        String hql = "SELECT count(c) FROM Club c WHERE c.validado = false";
        long count = (long) session.createQuery(hql).uniqueResult();
        
        session.getTransaction().commit();
        return count;
    }
}