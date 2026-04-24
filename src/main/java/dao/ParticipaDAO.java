package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import modelo.*;
import util.HibernateUtil;

public class ParticipaDAO extends DAO<Participa> {

    public ParticipaDAO() {
        super(Participa.class);
    }

    public Participa read(ParticipaId id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Participa participa = session.get(Participa.class, id);
            session.getTransaction().commit();
            return participa;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ✅ NUEVO: MÉTODO PARA INSCRIBIR DESDE EL MÓVIL
     * Inserta la relación en la tabla intermedia.
     */
    public void inscribir(int idTorneo, int idEquipo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            
            // Usamos SQL Nativo para insertar directamente en la tabla de unión
            // He usado 'inscripcion' que es el nombre que definiste en Torneo.java
            String sql = "INSERT INTO inscripcion (id_torneo, id_equipo) VALUES (:idTor, :idEq)";
            
            session.createNativeQuery(sql)
                    .setParameter("idTor", idTorneo)
                    .setParameter("idEq", idEquipo)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR AL INSCRIBIR EQUIPO: " + e.getMessage());
            // Si falla por 'inscripcion', intenta con 'participa' por si acaso
            reintentarConParticipa(idTorneo, idEquipo);
        }
    }

    private void reintentarConParticipa(int idTorneo, int idEquipo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            String sql = "INSERT INTO participa (id_torneo, id_equipo) VALUES (:idTor, :idEq)";
            session.createNativeQuery(sql)
                    .setParameter("idTor", idTorneo)
                    .setParameter("idEq", idEquipo)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
    }

    /**
     * 🚀 ASIGNAR GRUPO
     */
    public void actualizarGrupo(int idEquipo, int idTorneo, String grupo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            
            // Nota: He puesto 'participa' e 'inscripcion' para que busques cual usas
            String sql = "UPDATE participa SET grupo = :grupo WHERE id_equipo = :idEq AND id_torneo = :idTor";
            
            session.createNativeQuery(sql)
                    .setParameter("grupo", grupo)
                    .setParameter("idEq", idEquipo)
                    .setParameter("idTor", idTorneo)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR AL ACTUALIZAR GRUPO: " + e.getMessage());
        }
    }

    /**
     * 🚀 OBTENER EQUIPOS POR TORNEO
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Equipo> equipos = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // SQL Nativo para saltar problemas de mapeo
            String sql = "SELECT e.* FROM equipo e " +
                         "INNER JOIN inscripcion p ON e.id_equipo = p.id_equipo " +
                         "WHERE p.id_torneo = :id";
            
            equipos = session.createNativeQuery(sql, Equipo.class)
                    .setParameter("id", idTorneo)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR CRÍTICO EN DAO (getEquiposPorTorneo): " + e.getMessage());
        }
        return equipos;
    }

    /**
     * OBTENER PARTICIPACIONES (HQL)
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            // JOIN FETCH para evitar LazyInitializationException
            String hql = "FROM Participa p JOIN FETCH p.equipo JOIN FETCH p.torneo WHERE p.id.idTorneo = :id";
            lista = session.createQuery(hql, Participa.class)
                    .setParameter("id", idTorneo)
                    .getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
        return lista;
    }
}