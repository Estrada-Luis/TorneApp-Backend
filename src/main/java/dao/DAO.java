package dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class DAO<T> {

    private Class<T> clase;

    public DAO(Class<T> clase) {
        this.clase = clase;
    }

    // Método privado para gestionar la apertura segura de transacciones
    private Transaction safeBeginTransaction(Session session) {
        Transaction tx = session.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        return tx;
    }

    public void create(T t) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = safeBeginTransaction(session);
        try {
            session.save(t);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public T read(Serializable id) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = safeBeginTransaction(session);
        try {
            T t = session.get(clase, id);
            tx.commit();
            return t;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void update(T t) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = safeBeginTransaction(session);
        try {
            session.update(t);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void delete(T t) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = safeBeginTransaction(session);
        try {
            session.delete(t);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<T> readAll() {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = safeBeginTransaction(session);
        try {
            List<T> lista = session
                    .createQuery("from " + clase.getSimpleName(), clase)
                    .list();
            tx.commit();
            return lista;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    /**
     * Busca registros basados en una propiedad específica (Ej: email, nombre, etc.)
     * Fundamental para el Login y búsquedas filtradas desde el móvil.
     */
    public List<T> readByProperty(String nombrePropiedad, Object valor) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = safeBeginTransaction(session);
        try {
            // HQL dinámico: "from Usuario where email = :valor"
            String hql = "from " + clase.getSimpleName() + " where " + nombrePropiedad + " = :valor";
            List<T> lista = session.createQuery(hql, clase)
                                   .setParameter("valor", valor)
                                   .list();
            tx.commit();
            return lista;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
